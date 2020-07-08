package app.k8ty.api.models

import io.circe.Encoder
import io.circe.generic.semiauto.deriveEncoder

case class CoffeeFriends (
    id: Int,
    firstName: Option[String],
    lastName: Option[String],
    address1: Option[String],
    address2: Option[String],
    city: Option[String],
    state: Option[String],
    zip: Option[String],
    email: Option[String],
    userId: Option[String]
)

object CoffeeFriends {
    implicit val greenBeanEncoder: Encoder[CoffeeFriends] = deriveEncoder[CoffeeFriends]
}