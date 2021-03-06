package app.k8ty.api.http

import akka.actor.ActorSystem
import akka.http.scaladsl.Http
import akka.http.scaladsl.server.Directives._
import app.k8ty.api.config.Configuration.HttpServerConfig
import app.k8ty.api.environment.Environments.AppEnvironment
import app.k8ty.api.http.endpoints.{CalibanEndpoint, CoffeeRoastsEndpoint, HealthEndpoint}
import caliban.interop.circe.AkkaHttpCirceAdapter
import cats.effect.ExitCode
import zio._
import zio.console._
import ch.megard.akka.http.cors.scaladsl.CorsDirectives._

import scala.concurrent.{ExecutionContextExecutor, Future}

object Server extends AkkaHttpCirceAdapter {
  type ServerRIO[A] = RIO[AppEnvironment, A]

  def runServer: ServerRIO[ExitCode] =
    ZIO
    .runtime[AppEnvironment]
    .flatMap { implicit rts =>
      val cfg = rts.environment.get[HttpServerConfig]

      for {
        routes <- createAkkaHttpRoutes
        as <- Managed.make(Task(ActorSystem("k8ty")))(sys => Task.fromFuture(_ => sys.terminate()).ignore).use { implicit actorSystem =>
          implicit val executionContext: ExecutionContextExecutor = actorSystem.dispatcher
          val bindingFuture: Future[Http.ServerBinding] =
            Http().newServerAt(cfg.host, cfg.port).bind(routes)
          putStrLn(s"Running on ${cfg.host}:${cfg.port}\nPress ENTER to exit") *> getStrLn
        }
      } yield ExitCode.Success

    }.orDie


  def createAkkaHttpRoutes = {
    val healthRoutes = new HealthEndpoint[AppEnvironment].routes
    val coffeeRoutes = new CoffeeRoastsEndpoint[AppEnvironment].routes
    val calibanRoutes = new CalibanEndpoint[AppEnvironment].routes
    for {
      health <- healthRoutes
      coffee <- coffeeRoutes
      caliban <- calibanRoutes
      routes <- Task.succeed {
        cors() {
          concat (
            health,
            coffee,
            caliban
          )
        }
      }
    } yield routes

  }

}
