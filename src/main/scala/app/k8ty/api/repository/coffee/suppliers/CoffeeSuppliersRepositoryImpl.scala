package app.k8ty.api.repository.coffee.suppliers

import doobie.quill.DoobieContext
import doobie.util.transactor.Transactor
import io.getquill.{EntityQuery, Ord, Query, SnakeCase}
import zio.Task
import doobie.implicits._
import zio.interop.catz._

private[suppliers] final case class CoffeeSuppliersRepositoryImpl (xa: Transactor[Task])
extends CoffeeSuppliersRepository.Service {

  val ctx: DoobieContext.Postgres[SnakeCase.type] = new DoobieContext.Postgres(SnakeCase)
  import ctx._

  override def allSuppliers: Task[Seq[CoffeeSuppliers]] = {
    ctx.run(Queries.allSuppliers).transact(xa)
  }

  override def supplierById(id: Int): Task[Option[CoffeeSuppliers]] = {
    ctx.run(Queries.supplierById(id)).transact(xa).map(_.headOption)
  }

  private object Queries {
    val allSuppliers: ctx.Quoted[Query[CoffeeSuppliers]] = quote(query[CoffeeSuppliers].sortBy(_.id)(Ord.asc))

    def supplierById(id: Int): ctx.Quoted[EntityQuery[CoffeeSuppliers]] = quote{
      query[CoffeeSuppliers].filter(_.id == lift(id))
    }
  }

}
