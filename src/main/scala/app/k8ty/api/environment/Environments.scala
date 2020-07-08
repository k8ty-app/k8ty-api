package app.k8ty.api.environment

import app.k8ty.api.config.Configuration
import app.k8ty.api.config.Configuration._

import zio._
import zio.clock.Clock
import app.k8ty.api.repository.CoffeeRepository
import app.k8ty.api.repository.DbTransactor

object Environments {
    type HttpServerEnvironment = Configuration with Clock
    type AppEnvironment = HttpServerEnvironment with CoffeeRepository

    val httpServerEnvironment: ULayer[HttpServerEnvironment] = Configuration.live ++ Clock.live
    val dbTransactor: ULayer[DbTransactor] = Configuration.live >>> DbTransactor.postgres
    val coffeeRepository: ULayer[CoffeeRepository] = dbTransactor >>> CoffeeRepository.live
    val appEnvironment: ULayer[AppEnvironment] = httpServerEnvironment ++ coffeeRepository

}