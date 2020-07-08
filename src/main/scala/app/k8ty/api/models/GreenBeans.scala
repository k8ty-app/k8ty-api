package app.k8ty.api.models

import io.circe.Encoder
import io.circe.generic.semiauto.deriveEncoder

final case class GreenBeans(
    id: Int,
    supplierId: Option[Int],
    lotIdentifier: Option[String],
    purchaseWeightLb: Option[Int],
    purchasePrice: Option[Double],
    name: Option[String]
)

object GreenBeans {
    implicit val greenBeanEncoder: Encoder[GreenBeans] = deriveEncoder[GreenBeans]
}