package app.k8ty.api.http.endpoints

import akka.http.scaladsl.server.Directives.{path, _}
import akka.http.scaladsl.server.Route
import zio.{RIO, Task}

final class HealthEndpoint[R] {
  type HealthTask[A] = RIO[R, A]
  private val prefixPath = "_health"

  val routes: HealthTask[Route] = {
    for {
      routes <- Task.succeed {
        path(prefixPath) {
          complete("")
        }
      }
    } yield routes
  }
}
