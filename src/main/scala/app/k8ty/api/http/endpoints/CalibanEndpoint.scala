package app.k8ty.api.http.endpoints

import cats.effect.Blocker
import org.http4s.circe.CirceEntityCodec._
import org.http4s.dsl.Http4sDsl
import org.http4s.server.Router
import org.http4s.{HttpRoutes, Response, StaticFile}
import zio._
import zio.blocking.Blocking
import zio.interop.catz._

final class CalibanEndpoint[R <: ZEnv] {
  type CalibanTask[A] = RIO[R, A]
  private val prefixPath = "/caliban"


  val dsl: Http4sDsl[CalibanTask] = Http4sDsl[CalibanTask]
  import dsl._

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

  }

  val routes: HttpRoutes[CalibanTask] = Router(
    prefixPath -> calibanRoutes
  )

}
