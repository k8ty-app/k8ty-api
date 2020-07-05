package app.k8ty.api


import app.k8ty.api.http.Server
import app.k8ty.api.environment.Environments.appEnvironment
import zio._

object Main extends App {
  def run(args: List[String]): ZIO[ZEnv, Nothing, ExitCode] = {

    val program = for {
      _ <- Server.runServer
    } yield ()

    program.provideLayer(appEnvironment).exitCode

  }
}
