package app.k8ty.api.http.endpoints

import akka.http.scaladsl.server.Directives.{path, _}
import app.k8ty.api.repository.coffee.roasts.CoffeeRoasts
import app.k8ty.api.repository.coffee.roasts.CoffeeRoastsRepository._
import caliban.interop.circe.AkkaHttpCirceAdapter
import zio.{RIO, Task}

final class CoffeeRoastsEndpoint[R <: CoffeeRoastsRepository] extends AkkaHttpCirceAdapter{
  type CoffeeTask[A] = RIO[R,A]
  type CoffeeRoastStream = fs2.Stream[CoffeeTask, CoffeeRoasts]

  private val prefixPath = "coffee"


  val routes = {
    for {
      arTask <- allRoasts
      ar <- arTask
      routes <- Task.succeed {
        path (prefixPath / "roasts") {
          complete(ar)
        }
      }
    } yield  routes
  }

}
