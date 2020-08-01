package app.k8ty.api.config

import pureconfig.ConfigSource
import pureconfig.generic.auto._
import zio._

object Configuration {

    // Configuration for out DB
    final case class DbConfig(driver: String, url: String)
    // Configuration for out http4s server
    final case class HttpServerConfig(host: String, port: Int, path: String)

    // A global Config, that right now only contains our http4s server config
    final case class AppConfig(httpServer: HttpServerConfig, database: DbConfig)


    val live: ULayer[Configuration] = ZIO.effect(ConfigSource.default.loadOrThrow[AppConfig]) // Load our Configuration
      .map(c => Has(c.httpServer) ++ Has(c.database)) // Map it to Has out case classes
      .orDie.toLayerMany

}
