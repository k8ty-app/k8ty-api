package app.k8ty.api.repository.coffee.suppliers

import app.k8ty.api.repository.DbTransactor
import zio._

object CoffeeSuppliersRepository {

  trait Service {
    def allSuppliers: Task[Seq[CoffeeSuppliers]]
    def supplierById(id: Int): Task[Option[CoffeeSuppliers]]
  }

  type CoffeeSuppliersRepository = Has[Service]

  def allSuppliers: RIO[CoffeeSuppliersRepository, Task[Seq[CoffeeSuppliers]]] =
    ZIO.access(_.get.allSuppliers)

  def supplierById(id: Int): RIO[CoffeeSuppliersRepository, Task[Option[CoffeeSuppliers]]] =
    ZIO.access(_.get.supplierById(id))

  val live: URLayer[DbTransactor, CoffeeSuppliersRepository] = ZLayer.fromService { resource =>
    CoffeeSuppliersRepositoryImpl(resource.xa)
  }

}
