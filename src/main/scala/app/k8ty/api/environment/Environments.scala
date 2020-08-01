package app.k8ty.api.environment

import app.k8ty.api.config.Configuration
import app.k8ty.api.http.graphql.CalibanExampleService
import app.k8ty.api.http.graphql.CalibanExampleService._
import app.k8ty.api.http.graphql.CalibanExampleData._
import zio._
import zio.clock.Clock
import app.k8ty.api.repository._
import app.k8ty.api.repository.coffee.roasts._

object Environments {
    type HttpServerEnvironment = Configuration with Clock
    type AppEnvironment = ZEnv with HttpServerEnvironment with CoffeeRoastsRepository with CalibanExampleService

    val httpServerEnvironment: ULayer[HttpServerEnvironment] = Configuration.live ++ Clock.live
    val dbTransactor: ULayer[DbTransactor] = Configuration.live >>> DbTransactor.postgres
    val coffeeRoastsRepository: ULayer[CoffeeRoastsRepository] = dbTransactor >>> CoffeeRoastsRepository.live
    val caliban: ULayer[CalibanExampleService] =  CalibanExampleService.make(sampleCharacters)
    val appEnvironment = ZEnv.live ++ httpServerEnvironment ++ coffeeRoastsRepository ++ caliban

}
