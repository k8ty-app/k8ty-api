package app.k8ty.api.repository.coffee.roasts

import app.k8ty.api.repository.DbTransactor
import zio._

object CoffeeRoastsRepository {

  trait Service {
    def allRoasts: fs2.Stream[Task, CoffeeRoasts]
    def roastById(id: Int): Task[Option[CoffeeRoasts]]
  }

  val live: URLayer[DbTransactor, CoffeeRoastsRepository] = ZLayer.fromService { resource =>
    CoffeeRoastsRepositoryImpl(resource.xa)
  }

}
