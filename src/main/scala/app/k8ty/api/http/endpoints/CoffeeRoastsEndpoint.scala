package app.k8ty.api.http.endpoints

import app.k8ty.api.repository.coffee.roasts._
import cats.Applicative
import io.circe.Encoder
import io.circe.syntax._
import org.http4s.circe._
import org.http4s.dsl.Http4sDsl
import org.http4s.server.Router
import org.http4s.{EntityEncoder, HttpRoutes}
import zio.RIO
import zio.interop.catz._

final class CoffeeRoastsEndpoint[R <: CoffeeRoastsRepository] {
  type CoffeeTask[A] = RIO[R,A]
  type CoffeeRoastStream = fs2.Stream[CoffeeTask, CoffeeRoasts]

  implicit def jsonEncoder[F[_] : Applicative, A](implicit encoder: Encoder[A]): EntityEncoder[F, A] =
    jsonEncoderOf[F, A]
  private val prefixPath = "/coffee/roasts"

  val dsl: Http4sDsl[CoffeeTask] = Http4sDsl[CoffeeTask]
  import dsl._

  private val httpRoutes = HttpRoutes.of[CoffeeTask] {

    case GET -> Root =>  {
      val pipeline: CoffeeTask[CoffeeRoastStream] = allRoasts
      for {
        stream <- pipeline
        json <- Ok(stream.map(_.asJson))
      } yield json
    }

    case GET -> Root / IntVar(id) => {
      for {
        roast <- roastById(id)
        json <- Ok(roast.map(_.asJson))
      } yield json
    }
  }

  val routes: HttpRoutes[CoffeeTask] = Router(
    prefixPath -> httpRoutes
  )

}
