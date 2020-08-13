package app.k8ty.api.repository.coffee

import zio.{Has, RIO, Task}

package object roasts {

  type CoffeeRoastsRepository = Has[CoffeeRoastsRepository.Service]
  def allRoasts: RIO [CoffeeRoastsRepository, Task[Seq[CoffeeRoasts]]]= RIO.access(_.get.allRoasts)
  def roastById(id: Int): RIO[CoffeeRoastsRepository, Task[Option[CoffeeRoasts]]] = RIO.access(_.get.roastById(id))

}


