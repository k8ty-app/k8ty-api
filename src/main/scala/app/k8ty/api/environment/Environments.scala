package app.k8ty.api.environment

import app.k8ty.api.config.Configuration
import app.k8ty.api.config.Configuration._

import zio._
import zio.clock.Clock

object Environments {
    type HttpServerEnvironment = HasConfiguration with Clock
    type AppEnvironment = HttpServerEnvironment

    val httpServerEnvironment: ULayer[HttpServerEnvironment] = Configuration.live ++ Clock.live
    val appEnvironment: ULayer[AppEnvironment] = httpServerEnvironment

}