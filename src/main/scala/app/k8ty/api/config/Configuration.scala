package app.k8ty.api.config

import pureconfig.ConfigSource
import pureconfig.generic.auto._
import zio._

object Configuration {

    type HasConfiguration = Has[HttpServerConfig]

    // Confuratuon for out http4s server
    final case class HttpServerConfig(host: String, port: Int, path: String)

    // A global Config, that right now only contains our http4s server config
    final case class AppConfig(httpServer: HttpServerConfig)


    val live: ULayer[HasConfiguration] = ZLayer.fromEffectMany(
        ZIO.effect(ConfigSource.default.loadOrThrow[AppConfig]) // Load our Configuration
        .map(c => Has(c.httpServer)) // Map it to Has out case classes
        .orDie 
    )

}