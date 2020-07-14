package app.k8ty.api.repository.coffee.suppliers

import io.circe.Encoder
import io.circe.generic.semiauto.deriveEncoder

case class CoffeeSuppliers(
    id: Int,
    name: Option[String]
)

object CoffeeSuppliers {
    implicit val greenBeanEncoder: Encoder[CoffeeSuppliers] = deriveEncoder[CoffeeSuppliers]
}
