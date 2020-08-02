package app.k8ty.api.http.endpoints

import app.k8ty.api.environment.Environments.AppEnvironment
import app.k8ty.api.http.graphql.CalibanExampleApi
import caliban.Value.NullValue
import caliban.{GraphQLRequest, GraphQLResponse}
import cats.effect.Blocker
import org.http4s.circe.CirceEntityCodec._
import org.http4s.dsl.Http4sDsl
import org.http4s.server.Router
import org.http4s.{HttpRoutes, Response, StaticFile}
import zio._
import zio.blocking.Blocking
import zio.interop.catz._
import io.circe.Decoder.Result
import io.circe.Json
import io.circe.parser._
import io.circe.syntax._

final class CalibanEndpoint[R <: AppEnvironment] {
  type CalibanTask[A] = RIO[R, A]
  private val prefixPath = "/caliban"


  val dsl: Http4sDsl[CalibanTask] = Http4sDsl[CalibanTask]
  import dsl._

  // This is all largely lifted from caliban.Http4sAdapter

  private def getGraphQLRequest(
                                 query: String,
                                 op: Option[String],
                                 vars: Option[String],
                                 exts: Option[String]
                               ): Result[GraphQLRequest] = {
    val variablesJs  = vars.flatMap(parse(_).toOption)
    val extensionsJs = exts.flatMap(parse(_).toOption)
    val fields = List("query" -> Json.fromString(query)) ++
      op.map(o => "operationName"         -> Json.fromString(o)) ++
      variablesJs.map(js => "variables"   -> js) ++
      extensionsJs.map(js => "extensions" -> js)
    Json
      .fromFields(fields)
      .as[GraphQLRequest]
  }
  private def getGraphQLRequest(params: Map[String, String]): Result[GraphQLRequest] =
    getGraphQLRequest(
      params.getOrElse("query", ""),
      params.get("operationName"),
      params.get("variables"),
      params.get("extensions")
    )

  private val calibanRoutes = HttpRoutes.of[CalibanTask]{

    case request @ GET -> Root / "graphiql" => {
      val response: ZIO[R, Throwable, Response[CalibanTask]] = for {
        blocker: Blocker <- ZIO.access[Blocking](_.get.blockingExecutor.asEC)
          .map(Blocker.liftExecutionContext)
        file: Response[CalibanTask] <- StaticFile.fromResource("/graphiql.html", blocker, Some(request))
          .getOrElseF(NotFound(""))
      } yield {
        file
      }
      response
    }

    case request @POST -> Root / "api"  => {
      for {
        interpreter <- CalibanExampleApi.api.interpreter
        query <- request.attemptAs[GraphQLRequest].value.absolve
        result <- interpreter
          .executeRequest(query, skipValidation = false, enableIntrospection = true)
          .foldCause(cause => GraphQLResponse(NullValue, cause.defects).asJson, _.asJson)
        response <- Ok(result)
      } yield response
    }

    case request @GET -> Root / "api" => {
      for {
        interpreter <- CalibanExampleApi.api.interpreter
        query <- Task.fromEither(getGraphQLRequest(request.params))
        result <- interpreter
          .executeRequest(query, skipValidation = false, enableIntrospection = true)
          .foldCause(cause => GraphQLResponse(NullValue, cause.defects).asJson, _.asJson)
        response <- Ok(result)
      } yield response
    }

  }

  val routes: HttpRoutes[CalibanTask] = Router(
    prefixPath -> calibanRoutes
  )

}
