package app.k8ty.api.repository.coffee.roasts

import io.circe.Encoder
import io.circe.generic.semiauto.deriveEncoder

case class CoffeeRoasts(
    id: Int,
    batchNumber: Option[String],
    roastDate: Option[String],
    startG: Option[Int],
    endG: Option[Int],
    greenId: Option[Int],
    roastDuration: Option[String],
    beanTempC: Option[Seq[Double]],
    drumTempC: Option[Seq[Double]],
    beanDerivative: Option[Seq[Double]]
)

object CoffeeRoasts {
  implicit val coffeeRoastEncoder: Encoder[CoffeeRoasts] = deriveEncoder[CoffeeRoasts]
}
