package app.k8ty.api.http.endpoints

import java.io.File
import java.util.concurrent.Executors

import cats.data.Kleisli
import cats.effect.Blocker
import org.http4s.{EntityEncoder, HttpRoutes, Response, StaticFile}
import org.http4s.dsl.Http4sDsl
import org.http4s.implicits._
import org.http4s.server.Router
import zio._
import zio.blocking.{Blocking, _}
import zio.interop.catz._

import scala.io.{Codec, Source}


final class CalibanEndpoint[R <: ZEnv] {
  type CalibanTask[A] = RIO[R, A]
  private val prefixPath = "/caliban/api"

  val dsl: Http4sDsl[CalibanTask] = Http4sDsl[CalibanTask]
  import dsl._

  private val calibanRoutes = HttpRoutes.of[CalibanTask]{

    case request @ GET -> Root / "graphiql" => {
      for {
        blocker: Blocker <- ZIO.access[Blocking](_.get.blockingExecutor.asEC)
          .map(Blocker.liftExecutionContext)
        file: Response[CalibanTask] <- StaticFile.fromResource("/graphiql.html", blocker, Some(request))
          .getOrElseF(NotFound(""))
      } yield {
        file
      }
    }

  }

  val routes: HttpRoutes[CalibanTask] = Router(
    prefixPath -> calibanRoutes
  )

}
