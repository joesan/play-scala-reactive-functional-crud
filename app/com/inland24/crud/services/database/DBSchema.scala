package com.inland24.crud.services.database

import java.sql.Timestamp

import com.inland24.crud.services.database.models.OrganizationRow
import org.joda.time.{DateTime, DateTimeZone}
import slick.jdbc.JdbcProfile


final class DBSchema private (val driver: JdbcProfile) {

  import driver.api._

  /**
    * Mapping for using Joda Time.
    */
  implicit def dateTimeMapping = MappedColumnType.base[DateTime, java.sql.Timestamp](
    dt => new Timestamp(dt.getMillis),
    ts => new DateTime(ts.getTime, DateTimeZone.UTC)
  )

  /**
    * The Sensor details are maintained in the sensor table
    */
  class OrganizationTable(tag: Tag) extends Table[OrganizationRow](tag, "organization") {
    def id            = column[Int]("id", O.PrimaryKey, O.AutoInc)
    def orgName       = column[String]("organizationName")
    def orgAddressId  = column[Int]("organizationAddressId")
    def isActive      = column[Boolean]("isActive")
    def createdAt     = column[DateTime]("created_at")
    def updatedAt     = column[DateTime]("updated_at")

    def * = {
      (id, orgName, orgAddressId, isActive, createdAt, updatedAt) <>
        (OrganizationRow.tupled, OrganizationRow.unapply)
    }
  }

  object OrganizationTable {
    /**
      * A TableQuery can be used for composing queries, inserts
      * and pretty much anything related to the sensors table.
      */
    val all = TableQuery[OrganizationTable]

    /**
      * Query to filter and fetch all operational sensors
      */
    val allActiveOrganizations = {
      all.filter(_.isActive === true)
    }

    /**
      * Query to filter and fetch all operational sensors
      */
    val organizationById = (id: Int) => {
      all.filter(_.id === id)
    }
  }
}
object DBSchema {
  def apply(driver: JdbcProfile) = {
    new DBSchema(driver)
  }
}