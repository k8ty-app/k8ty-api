package app.k8ty.api.repository.coffee.roasts

import app.k8ty.api.repository.DbTransactor
import zio._

object CoffeeRoastsRepository {

  trait Service {
    def allRoasts: Task[Seq[CoffeeRoasts]]
    def roastById(id: Int): Task[Option[CoffeeRoasts]]
  }

  def allRoasts: RIO [CoffeeRoastsRepository, Task[Seq[CoffeeRoasts]]]= RIO.access(_.get.allRoasts)
  def roastById(id: Int): RIO[CoffeeRoastsRepository, Task[Option[CoffeeRoasts]]] = RIO.access(_.get.roastById(id))

  val live: URLayer[DbTransactor, CoffeeRoastsRepository] = ZLayer.fromService { resource =>
    CoffeeRoastsRepositoryImpl(resource.xa)
  }

  type CoffeeRoastsRepository = Has[Service]

}

