package app.k8ty.api

import app.k8ty.api.config.Configuration.{DbConfig, HttpServerConfig}
import zio.Has

package object config {
  type Configuration = Has[DbConfig] with Has[HttpServerConfig]
}
