package app.k8ty.api


import app.k8ty.api.http.Server
import zio._

object Main extends App {
  def run(args: List[String]): ZIO[ZEnv, Nothing, ExitCode] = {

    val program: ZIO[ZEnv, Throwable, Unit] = for {
      _ <- Server.runServer
    } yield ()

    program
      .as(ExitCode(0))
      .catchAll(x => console.putStrLn(s"Exception: $x").as(ExitCode(1)))

  }
}
