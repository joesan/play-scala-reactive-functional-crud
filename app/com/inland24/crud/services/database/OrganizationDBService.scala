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

import com.inland24.crud.config.DBConfig
import com.inland24.crud.services.database.models.OrganizationRow

import scala.concurrent.{ExecutionContext, Future}

import scala.async.Async._


class OrganizationDBService(val dbConfig: DBConfig)(implicit ec: ExecutionContext) { self =>

  private val schema = DBSchema(dbConfig.slickDriver)
  private val database = dbConfig.database

  /** Note: These imports should be here! Do not move it */
  import schema._
  import schema.driver.api._

  // Organization related CRUD services //
  def fetchOrganizations(fetchOnlyActive: Boolean = true): Future[Seq[OrganizationRow]] = {
    val query = if (fetchOnlyActive)
      OrganizationTable.allActiveOrganizations
    else OrganizationTable.all

    database.run(query.result)
  }

  def fetchSensorById(id: Int): Future[Option[OrganizationRow]] = {
    database.run(OrganizationTable.organizationById(id).result.headOption)
  }

  def createNewSensor(orgRow: OrganizationRow): Future[Int] = async {
    val q = OrganizationTable.all
    val sql = (q returning q.map(_.id)) += orgRow

    await(database.run(sql))
  }

  def deleteSensorById(orgId: Int): Future[Int] = {
    database.run(OrganizationTable.organizationById(orgId).delete)
  }
}