package app.k8ty.api.repository

import app.k8ty.api.repository.models.CoffeeRoasts
import doobie.implicits._
import zio.interop.catz._
import doobie.quill.DoobieContext
import doobie.util.transactor.Transactor
import io.getquill._
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

private final case class CoffeeRoastsRepositoryImpl(xa: Transactor[Task])
extends CoffeeRoastsRepository.Service {

  val ctx: DoobieContext.Postgres[SnakeCase.type] = new DoobieContext.Postgres(SnakeCase)
  import ctx._

  override def allRoasts: fs2.Stream[Task, CoffeeRoasts] = {
    val _query = quote(query[CoffeeRoasts].sortBy(_.id)(Ord.desc))
    ctx.stream(_query).transact(xa)
  }

  override def roastById(id: Int): Task[Option[CoffeeRoasts]] = {
    def _query(id: Int): ctx.Quoted[EntityQuery[CoffeeRoasts]] = quote {
      query[CoffeeRoasts].filter(_.id == lift(id))
    }
    ctx.run(_query(id)).transact(xa).map(_.headOption)
  }
}


