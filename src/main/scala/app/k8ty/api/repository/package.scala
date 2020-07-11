package app.k8ty.api

import app.k8ty.api.config.Configuration.DbConfig
import doobie.util.transactor.Transactor
import zio._
import zio.interop.catz._

package object repository extends
  CoffeeRoastsRepositoryPackage {

  type DbTransactor = Has[DbTransactor.Resource]

  object DbTransactor {
    trait Resource {
      val xa: Transactor[Task]
    }

    val postgres: URLayer[Has[DbConfig], DbTransactor] = ZLayer.fromService { db =>
      new Resource {
        // Heroku sets DATABASE_URL, but leaves off the jdbc: and isn't postgresql:...
        // DATABASE_URL = postgres://user:pw@url/database
        val herokuUrl: String = db.url
        val _url: String = s"jdbc:postgresql://${herokuUrl.split("@").last}"
        val _userPass: Array[String] = herokuUrl.split("//").last.split("@").head.split(":")
        val xa: Transactor[Task] =
          Transactor.fromDriverManager(db.driver, _url, _userPass.head, _userPass.last)
      }
    }
  }
}
