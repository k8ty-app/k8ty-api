package app.k8ty.api.http.endpoints

import cats.Applicative
import io.circe.Encoder
import org.http4s.{EntityEncoder, HttpRoutes, Status}
import org.http4s.dsl.Http4sDsl
import org.http4s.server.Router
import zio.interop.catz._
import zio.RIO
import org.http4s.circe.jsonEncoderOf

final class HealthEndpoint[R] {
  type HealthTask[A] = RIO[R, A]
  implicit def jsonEncoder[F[_] : Applicative, A](implicit encoder: Encoder[A]): EntityEncoder[F, A] =
    jsonEncoderOf[F, A]
  private val prefixPath = "/_health"

  val dsl: Http4sDsl[HealthTask] = Http4sDsl[HealthTask]
  import dsl._

  private val httpRoutes = HttpRoutes.of[HealthTask] {
    case GET -> Root => Ok({})
  }

  val routes: HttpRoutes[HealthTask] = Router(
    prefixPath -> httpRoutes
  )
}
