package app.k8ty.api.http.endpoints

import akka.http.scaladsl.server.Directives.{getFromResource, path, _}
import akka.http.scaladsl.server.Route
import app.k8ty.api.environment.Environments.AppEnvironment
import app.k8ty.api.http.graphql.CalibanExampleApi
import caliban.interop.circe.AkkaHttpCirceAdapter
import caliban.{CalibanError, GraphQLInterpreter}
import zio._

import scala.concurrent.Future

final class CalibanEndpoint[R <: AppEnvironment] extends AkkaHttpCirceAdapter {
  type CalibanTask[A] = RIO[R, A]
  private val prefixPath = "caliban"

  val routes: CalibanTask[Route] = {
    for {
      runtime: Runtime[R] <- ZIO.runtime[R]
      interpreter: GraphQLInterpreter[R, CalibanError] <- CalibanExampleApi.api.interpreter
      api: Route <- ZIO.fromFuture { implicit ec =>
        Future.successful {
          adapter.makeHttpService(interpreter)(ec, runtime)
        }
      }
      graphiql: Route <- Task.succeed {
        getFromResource("graphiql.html")
      }
      routes: Route <- ZIO.succeed {
        path(prefixPath / "api") {
          api
        } ~ path(prefixPath / "graphiql") {
          graphiql
        }
      }
    } yield routes
  }

}
