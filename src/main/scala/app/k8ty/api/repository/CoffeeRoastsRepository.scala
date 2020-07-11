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
    ctx.stream(Queries.allRoasts).transact(xa)
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

trait CoffeeRoastsRepositoryPackage {
  type CoffeeRoastsRepository = Has[CoffeeRoastsRepository.Service]
  def allRoasts: RIO[CoffeeRoastsRepository, fs2.Stream[Task, CoffeeRoasts]] = RIO.access(_.get.allRoasts)
  def roastById(id: Int): RIO[CoffeeRoastsRepository, Task[Option[CoffeeRoasts]]] = RIO.access(_.get.roastById(id))
}

