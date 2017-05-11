/*
 * Copyright (c) 2017 joesan @ http://github.com/joesan
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *   http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */

package com.inland24.crud.services.database

import java.sql.Timestamp

import com.inland24.crud.services.database.models.{AddressRow, OrganizationRow}
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

  ///////////////// Organization Table
  /**
    * The Organization details are maintained in the organization table
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
      * Query to filter and fetch all active organizations
      */
    val allActiveOrganizations = {
      all.filter(_.isActive === true)
    }

    /**
      * Query to filter and fetch organization by a given id
      */
    val organizationById = (id: Int) => {
      all.filter(_.id === id)
    }
  }

  ///////////////// Address Table
  /**
    * The Address details are maintained in the Address table
    */
  class AddressTable(tag: Tag) extends Table[AddressRow](tag, "address") {
    def id        = column[Int]("id", O.PrimaryKey, O.AutoInc)
    def streetNum = column[Int]("streetNum")
    def street    = column[String]("street")
    def city      = column[String]("city")
    def plz       = column[Int]("plz")
    def country   = column[String]("country")

    def * = {
      (id, streetNum, street, city, plz, country) <>
        (AddressRow.tupled, AddressRow.unapply)
    }
  }

  object AddressTable {

    val all = TableQuery[AddressTable]

    val addressById = (id: Int) => {
      all.filter(_.id === id)
    }
  }
}
object DBSchema {
  def apply(driver: JdbcProfile) = {
    new DBSchema(driver)
  }
}