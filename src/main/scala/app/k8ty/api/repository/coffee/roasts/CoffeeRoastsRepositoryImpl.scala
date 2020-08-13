package app.k8ty.api.repository.coffee.roasts

import doobie.quill.DoobieContext
import doobie.util.transactor.Transactor
import io.getquill.{EntityQuery, Ord, Query, SnakeCase}
import zio.Task
import doobie.implicits._
import zio.interop.catz._


private[roasts] final case class CoffeeRoastsRepositoryImpl(xa: Transactor[Task])
  extends CoffeeRoastsRepository.Service {

  val ctx: DoobieContext.Postgres[SnakeCase.type] = new DoobieContext.Postgres(SnakeCase)
  import ctx._

  override def allRoasts: Task[Seq[CoffeeRoasts]] = {
    ctx.run(Queries.allRoasts).transact(xa)
  }

  override def roastById(id: Int): Task[Option[CoffeeRoasts]] = {
    ctx.run(Queries.roastById(id)).transact(xa).map(_.headOption)
  }

  private object Queries {

    val allRoasts: ctx.Quoted[Query[CoffeeRoasts]] = quote(query[CoffeeRoasts].sortBy(_.id)(Ord.desc))

    def roastById(id: Int): ctx.Quoted[EntityQuery[CoffeeRoasts]] = quote {
      query[CoffeeRoasts].filter(_.id == lift(id))
    }

  }

}
