package app.k8ty.api.repository

import app.k8ty.api.models.CoffeeRoasts
import zio._

object CoffeeRepository {

  trait Service {
    def allRoasts: fs2.Stream[Task, CoffeeRoasts]
    def roastById(id: Int): Task[Option[CoffeeRoasts]]
  }

  val live: URLayer[DbTransactor, CoffeeRepository] = ZLayer.fromService { resource =>
    Database(resource.xa)
  }
}
