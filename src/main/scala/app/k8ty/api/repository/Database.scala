package app.k8ty.api.repository

import doobie.implicits._
import doobie.postgres.implicits._
import doobie.quill.DoobieContext
import doobie.Meta
import doobie.util.transactor.Transactor
import io.getquill._
import zio.Task
import zio.interop.catz._
import app.k8ty.api.models.CoffeeRoasts

private[repository] final case class Database(xa: Transactor[Task])
    extends CoffeeRepository.Service {

  val ctx = new DoobieContext.Postgres(SnakeCase)
  import ctx._

  def allRoasts: fs2.Stream[Task, CoffeeRoasts] =
    ctx.stream(Queries.allRoasts).transact(xa)
    
  def roastById(id: Int): Task[Option[CoffeeRoasts]] = 
  ctx.run(Queries.roastById(id)).transact(xa).map(_.headOption)

  private object Queries {

    val allRoasts = quote(query[CoffeeRoasts])

    def roastById(id: Int) =
      quote {
        allRoasts.filter(_.id == lift(id))
      }

  }

}
