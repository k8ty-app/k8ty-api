package app.k8ty.api.http.graphql

import app.k8ty.api.http.graphql.CalibanService.CalibanService
import app.k8ty.api.repository.coffee.roasts.{CoffeeRoasts, CoffeeRoastsRepository}
import caliban.GraphQL.graphQL
import caliban.schema.Annotations.GQLDescription
import caliban.schema.GenericSchema
import caliban.wrappers.Wrappers.{maxDepth, maxFields, printSlowQueries, timeout, printErrors}
import caliban.{GraphQL, RootResolver}
import zio.clock.Clock
import zio.console.Console
import zio.{RIO, Task}
import zio.duration._
import caliban.wrappers.ApolloTracing.apolloTracing

object CalibanApi extends GenericSchema[CalibanService]{


  case class CoffeeRoastsArgs()
  case class CoffeeRoastArgs(id: Int)

  case class Queries(
                      @GQLDescription("Return all Coffee Roasts")
                      coffeeRoasts: CoffeeRoastsArgs => RIO[CalibanService, Task[Seq[CoffeeRoasts]]],
                      @GQLDescription("Return a Coffee Roast by it's ID")
                      coffeeRoast: CoffeeRoastArgs => RIO[CalibanService, Task[Option[CoffeeRoasts]]]
                    )

  implicit val coffeeRoastsSchema = gen[CoffeeRoasts]
  implicit val coffeeRoastArgsSchema = gen[CoffeeRoastArgs]

  val api: GraphQL[Console with Clock with CalibanService] =
    graphQL(
      RootResolver(
        Queries(
          _ => CoffeeRoastsRepository.allRoasts,
          args => CoffeeRoastsRepository.roastById(args.id)
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
