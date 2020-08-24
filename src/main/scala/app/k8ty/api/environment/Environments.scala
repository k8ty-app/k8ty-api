package app.k8ty.api.environment

import app.k8ty.api.config.Configuration
import app.k8ty.api.http.graphql.CalibanExampleService
import app.k8ty.api.http.graphql.CalibanExampleService._
import app.k8ty.api.http.graphql.CalibanExampleData._
import app.k8ty.api.http.graphql.CalibanService.CalibanService
import zio._
import zio.clock.Clock
import app.k8ty.api.repository._
import app.k8ty.api.repository.coffee.roasts.CoffeeRoastsRepository.CoffeeRoastsRepository
import app.k8ty.api.repository.coffee.roasts._
import app.k8ty.api.repository.coffee.suppliers.CoffeeSuppliersRepository
import app.k8ty.api.repository.coffee.suppliers.CoffeeSuppliersRepository.CoffeeSuppliersRepository

object Environments {
    type HttpServerEnvironment = Configuration with Clock
    type AppEnvironment = ZEnv
      with HttpServerEnvironment
      with CoffeeRoastsRepository
      with CoffeeSuppliersRepository
      with CalibanService

    val httpServerEnvironment: ULayer[HttpServerEnvironment] = Configuration.live ++ Clock.live
    val dbTransactor: ULayer[DbTransactor] = Configuration.live >>> DbTransactor.postgres
    val coffeeRoastsRepository: ULayer[CoffeeRoastsRepository] = dbTransactor >>> CoffeeRoastsRepository.live
    val coffeeSuppliersRepository: ULayer[CoffeeSuppliersRepository] = dbTransactor >>> CoffeeSuppliersRepository.live
    val appEnvironment = ZEnv.live ++
      httpServerEnvironment ++
      coffeeRoastsRepository ++
      coffeeSuppliersRepository

}
