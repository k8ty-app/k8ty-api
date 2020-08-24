package app.k8ty.api.http.graphql

import app.k8ty.api.http.graphql.CalibanService.CalibanService
import app.k8ty.api.repository.coffee.roasts.{CoffeeRoasts, CoffeeRoastsRepository}
import app.k8ty.api.repository.coffee.suppliers.{CoffeeSuppliers, CoffeeSuppliersRepository}
import caliban.GraphQL.graphQL
import caliban.schema.Annotations.GQLDescription
import caliban.schema.GenericSchema
import caliban.wrappers.Wrappers.{maxDepth, maxFields, printErrors, printSlowQueries, timeout}
import caliban.{GraphQL, RootResolver}
import zio.clock.Clock
import zio.console.Console
import zio.{RIO, Task}
import zio.duration._
import caliban.wrappers.ApolloTracing.apolloTracing

object CalibanApi extends GenericSchema[CalibanService]{


  case class CoffeeRoastsArgs()
  case class CoffeeRoastArgs(id: Int)
  case class CoffeeSuppliersArgs()
  case class CoffeeSupplierArgs(id: Int)

  case class Queries(
                      @GQLDescription("Return all Coffee Roasts")
                      coffeeRoasts: CoffeeRoastsArgs => RIO[CalibanService, Task[Seq[CoffeeRoasts]]],
                      @GQLDescription("Return a Coffee Roast by its ID")
                      coffeeRoast: CoffeeRoastArgs => RIO[CalibanService, Task[Option[CoffeeRoasts]]],
                      @GQLDescription("Return all Green Coffee Suppliers")
                      coffeeSuppliers: CoffeeSuppliersArgs => RIO[CalibanService, Task[Seq[CoffeeSuppliers]]],
                      @GQLDescription("Return a Green Coffee Supplier by its ID")
                      coffeeSupplier: CoffeeSupplierArgs => RIO[CalibanService, Task[Option[CoffeeSuppliers]]],
                    )

  implicit val coffeeRoastsSchema = gen[CoffeeRoasts]
  implicit val coffeeRoastArgsSchema = gen[CoffeeRoastArgs]
  implicit val coffeeSuppliersSchema = gen[CoffeeSuppliers]
  implicit val coffeeSupplierArgsSchema = gen[CoffeeSupplierArgs]

  val api: GraphQL[Console with Clock with CalibanService] =
    graphQL(
      RootResolver(
        Queries(
          _ => CoffeeRoastsRepository.allRoasts,
          args => CoffeeRoastsRepository.roastById(args.id),
          _ => CoffeeSuppliersRepository.allSuppliers,
          args => CoffeeSuppliersRepository.supplierById(args.id)
        )
      )
    ) @@
      maxFields(200) @@               // query analyzer that limit query fields
      maxDepth(30) @@                 // query analyzer that limit query depth
      timeout(3 seconds) @@           // wrapper that fails slow queries
      printErrors @@ // wrapper that prints errors
      printSlowQueries(500 millis) @@ // wrapper that logs slow queries
      apolloTracing

}
