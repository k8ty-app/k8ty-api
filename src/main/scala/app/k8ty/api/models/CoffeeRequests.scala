package app.k8ty.api.models

import io.circe.Encoder
import io.circe.generic.semiauto.deriveEncoder

case class CoffeeRequests(
    id: Int,
    dateRequested: Option[String],
    dateSent: Option[String],
    trackingInfo: Option[String],
    lbRequested: Option[Int],
    friendId: Option[Int],
    roastsSent: Option[Set[Int]]
)

object CoffeeRequests {
    implicit val greenBeanEncoder: Encoder[CoffeeRequests] = deriveEncoder[CoffeeRequests]
}