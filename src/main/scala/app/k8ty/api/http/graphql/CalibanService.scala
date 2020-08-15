package app.k8ty.api.http.graphql

import app.k8ty.api.repository.coffee.roasts.CoffeeRoastsRepository.CoffeeRoastsRepository

object CalibanService {

  type CalibanService = CoffeeRoastsRepository

  trait Service {

  }

}
