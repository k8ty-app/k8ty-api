package app.k8ty.api.http.graphql

import app.k8ty.api.http.graphql.CalibanExampleData.Origin
import zio._
import zio.stream._

object CalibanExampleService {

  type CalibanExampleService = Has[Service]

  trait Service {
    def getCharacters(origin: Option[Origin]): UIO[List[CalibanExampleData.Character]]

    def findCharacter(name: String): UIO[Option[CalibanExampleData.Character]]

    def deleteCharacter(name: String): UIO[Boolean]

    def deletedEvents: ZStream[Any, Nothing, String]
  }

  def getCharacters(origin: Option[Origin]): URIO[CalibanExampleService, List[CalibanExampleData.Character]] =
    URIO.accessM(_.get.getCharacters(origin))

  def findCharacter(name: String): URIO[CalibanExampleService, Option[CalibanExampleData.Character]] =
    URIO.accessM(_.get.findCharacter(name))

  def deleteCharacter(name: String): URIO[CalibanExampleService, Boolean] =
    URIO.accessM(_.get.deleteCharacter(name))

  def deletedEvents: ZStream[CalibanExampleService, Nothing, String] =
    ZStream.accessStream(_.get.deletedEvents)

  def make(initial: List[CalibanExampleData.Character]): ZLayer[Any, Nothing, CalibanExampleService] = ZLayer.fromEffect {
    for {
      characters  <- Ref.make(initial)
      subscribers <- Ref.make(List.empty[Queue[String]])
    } yield new Service {

      def getCharacters(origin: Option[Origin]): UIO[List[CalibanExampleData.Character]] =
        characters.get.map(_.filter(c => origin.forall(c.origin == _)))

      def findCharacter(name: String): UIO[Option[CalibanExampleData.Character]] = characters.get.map(_.find(c => c.name == name))

      def deleteCharacter(name: String): UIO[Boolean] =
        characters
          .modify(list =>
            if (list.exists(_.name == name)) (true, list.filterNot(_.name == name))
            else (false, list)
          )
          .tap(deleted =>
            UIO.when(deleted)(
              subscribers.get.flatMap(
                // add item to all subscribers
                UIO.foreach(_)(queue =>
                  queue
                    .offer(name)
                    .catchSomeCause {
                      case cause if cause.interrupted =>
                        subscribers.update(_.filterNot(_ == queue)).as(false)
                    } // if queue was shutdown, remove from subscribers
                )
              )
            )
          )

      def deletedEvents: ZStream[Any, Nothing, String] = ZStream.unwrap {
        for {
          queue <- Queue.unbounded[String]
          _     <- subscribers.update(queue :: _)
        } yield ZStream.fromQueue(queue).ensuring(queue.shutdown)
      }
    }
  }
}
