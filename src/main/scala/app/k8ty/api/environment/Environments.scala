package app.k8ty.api.environment

import app.k8ty.api.config.Configuration
import zio._
import zio.clock.Clock
import app.k8ty.api.repository._

object Environments {
    type HttpServerEnvironment = Configuration with Clock
    type AppEnvironment = HttpServerEnvironment with CoffeeRoastsRepository

    val httpServerEnvironment: ULayer[HttpServerEnvironment] = Configuration.live ++ Clock.live
    val dbTransactor: ULayer[DbTransactor] = Configuration.live >>> DbTransactor.postgres
    val coffeeRepository: ULayer[CoffeeRoastsRepository] = dbTransactor >>> CoffeeRoastsRepository.live
    val appEnvironment: ULayer[AppEnvironment] = httpServerEnvironment ++ coffeeRepository

}
