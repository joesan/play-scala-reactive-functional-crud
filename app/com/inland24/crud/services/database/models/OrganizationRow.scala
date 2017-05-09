package com.inland24.crud.services.database.models

import org.joda.time.DateTime


case class OrganizationRow(
  id           : Int,
  orgName      : String,
  orgAddressId : Int,
  isActive     : Boolean,
  createdAt    : DateTime,
  updatedAt    : DateTime
)