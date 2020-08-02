package app.k8ty.api.http

import app.k8ty.api.environment.Environments.AppEnvironment
import app.k8ty.api.config.Configuration.HttpServerConfig
import app.k8ty.api.http.endpoints.{CalibanEndpoint, CoffeeRoastsEndpoint, HealthEndpoint}
import cats.data.Kleisli
import cats.effect.ExitCode
import cats.implicits._
import org.http4s.implicits._
import org.http4s.server.blaze.BlazeServerBuilder
import org.http4s.server.Router
import org.http4s.server.middleware.{AutoSlash, CORS, CORSConfig, GZip}
import org.http4s.{HttpRoutes, Request, Response}
import zio.interop.catz._
import zio._

import scala.concurrent.duration._

object Server {
  type ServerRIO[A] = RIO[AppEnvironment, A]
  type ServerRoutes =
    Kleisli[ServerRIO, Request[ServerRIO], Response[ServerRIO]]

  def runServer: ZIO[AppEnvironment, Nothing, Unit] =
    ZIO
      .runtime[AppEnvironment]
      .flatMap { implicit rts =>
        val cfg = rts.environment.get[HttpServerConfig]
        val ec = rts.platform.executor.asEC

        BlazeServerBuilder[ServerRIO](ec)
          .bindHttp(cfg.port, cfg.host)
          .withHttpApp(createRoutes(cfg.path))
          .serve
          .compile[ServerRIO, ServerRIO, ExitCode]
          .drain
      }
      .orDie

  def createRoutes(basePath: String): ServerRoutes = {
    val healthRoutes: HttpRoutes[HealthEndpoint[AppEnvironment]#HealthTask] = new HealthEndpoint[AppEnvironment].routes
    val coffeeRoutes: HttpRoutes[CoffeeRoastsEndpoint[AppEnvironment]#CoffeeTask] = new CoffeeRoastsEndpoint[AppEnvironment].routes
    val calibanRoutes = new CalibanEndpoint[AppEnvironment].routes
    val routes = healthRoutes <+> coffeeRoutes <+> calibanRoutes

    Router[ServerRIO](basePath -> middleware(routes)).orNotFound
  }

  private val originConfig = CORSConfig(
    anyOrigin = false,
    allowedOrigins = Set("https://k8ty.app", "http://localhost:4200", "http://localhost:9000"),
    allowCredentials = false,
    maxAge = 1.day.toSeconds
  )

  private val middleware: HttpRoutes[ServerRIO] => HttpRoutes[ServerRIO] = {
    { http: HttpRoutes[ServerRIO] =>
      AutoSlash(http)
    }.andThen { http: HttpRoutes[ServerRIO] =>
      GZip(http)
    }.andThen { http: HttpRoutes[ServerRIO] =>
      CORS(http, originConfig)
    }
  }
}
