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