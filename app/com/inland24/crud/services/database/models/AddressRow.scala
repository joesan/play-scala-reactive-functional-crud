package com.inland24.crud.services.database.models


case class AddressRow (
  id        : Int,
  streetNum : Int,
  street    : String,
  city      : String,
  plz       : Int,
  country   : String
)