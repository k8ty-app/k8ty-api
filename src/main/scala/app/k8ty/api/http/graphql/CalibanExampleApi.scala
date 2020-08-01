package app.k8ty.api.http.graphql

import CalibanExampleData._
import CalibanExampleService.CalibanExampleService
import caliban.{GraphQL, RootResolver}
import caliban.GraphQL.graphQL
import caliban.schema.Annotations.{GQLDeprecated, GQLDescription}
import caliban.schema.GenericSchema
import caliban.wrappers.ApolloTracing.apolloTracing
import caliban.wrappers.Wrappers._
import zio._
import zio.clock.Clock
import zio.console.Console
import zio.stream._
import zio.duration._
import scala.language.postfixOps

object CalibanExampleApi extends GenericSchema[CalibanExampleService] {

  case class Queries(
                      @GQLDescription("Return all characters from a given origin")
    characters: CharactersArgs => URIO[CalibanExampleService, List[Character]],
                      @GQLDeprecated("Use `characters`")
    character: CharacterArgs => URIO[CalibanExampleService, Option[Character]]
  )
  case class Mutations(deleteCharacter: CharacterArgs => URIO[CalibanExampleService, Boolean])
  case class Subscriptions(characterDeleted: ZStream[CalibanExampleService, Nothing, String])

  implicit val roleSchema           = gen[Role]
  implicit val characterSchema      = gen[Character]
  implicit val characterArgsSchema  = gen[CharacterArgs]
  implicit val charactersArgsSchema = gen[CharactersArgs]

  val api: GraphQL[Console with Clock with CalibanExampleService] =
    graphQL(
      RootResolver(
        Queries(
          args => CalibanExampleService.getCharacters(args.origin),
          args => CalibanExampleService.findCharacter(args.name)
        ),
        Mutations(args => CalibanExampleService.deleteCharacter(args.name)),
        Subscriptions(CalibanExampleService.deletedEvents)
      )
    ) @@
      maxFields(200) @@               // query analyzer that limit query fields
      maxDepth(30) @@                 // query analyzer that limit query depth
      timeout(3 seconds) @@           // wrapper that fails slow queries
      printSlowQueries(500 millis) @@ // wrapper that logs slow queries
      apolloTracing                   // wrapper for https://github.com/apollographql/apollo-tracing

}
