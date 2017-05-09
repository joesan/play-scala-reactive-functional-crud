package com.inland24.crud.config

import java.util.concurrent.TimeUnit

import com.typesafe.config.Config
import play.api
import slick.jdbc.{JdbcBackend, JdbcProfile}
import slick.jdbc.JdbcBackend.Database

import scala.concurrent.duration._
import scala.concurrent.duration.FiniteDuration
import scala.util.Try


/**
  * Type-safe configuration used throughout the application.
  */
final case class AppConfig(
  database: DBConfig
)
final case class DBConfig(
  url: String,
  user: Option[String],
  password: Option[String],
  driver: String,
  refreshInterval: FiniteDuration) {

  lazy val slickDriver: JdbcProfile = driver match {
    case "org.sqlite.JDBC" =>
      Class.forName(driver)
      slick.jdbc.SQLiteProfile
    case "org.h2.Driver" =>
      Class.forName(driver)
      slick.jdbc.H2Profile
  }

  lazy val database: JdbcBackend.DatabaseDef = {
    Database.forURL(url, user.orNull, password.orNull, driver = driver)
  }
}
object AppConfig {
  def load(): AppConfig =
    load(ConfigUtil.loadFromEnv())

  def load(config: api.Configuration): AppConfig =
    load(config.underlying)

  def load(config: Config): AppConfig = {
    AppConfig(
      database = DBConfig(
        url = config.getString("dbConfig.url"),
        user = Try(config.getString("dbConfig.user")).toOption.filterNot(_.isEmpty),
        password = Try(config.getString("dbConfig.password")).toOption.filterNot(_.isEmpty),
        driver = config.getString("dbConfig.driver"),
        refreshInterval = config.getDuration("dbConfig.refreshInterval", TimeUnit.MILLISECONDS).millis
      )
    )
  }
}