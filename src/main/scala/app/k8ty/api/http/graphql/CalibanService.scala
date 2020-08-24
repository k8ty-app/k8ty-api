package app.k8ty.api.http.graphql

import app.k8ty.api.repository.coffee.roasts.CoffeeRoastsRepository.CoffeeRoastsRepository
import app.k8ty.api.repository.coffee.suppliers.CoffeeSuppliersRepository.CoffeeSuppliersRepository

object CalibanService {

  type CalibanService = CoffeeRoastsRepository with CoffeeSuppliersRepository

  trait Service {

  }

}
