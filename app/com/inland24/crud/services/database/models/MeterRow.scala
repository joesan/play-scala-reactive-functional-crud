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

package com.inland24.crud.services.database.models


case class MeterRow(
  meterId: String,
  orgId: Int,
  meterType: MeterType
)

sealed trait MeterType { def toString: String }
case object SmartMeter extends MeterType { override def toString = "SmartMeter" }
case object ShitMeter  extends MeterType { override def toString = "ShitMeter"  }
case object Unknown    extends MeterType { override def toString = "Unknown"    }

object MeterType {

  val allMeters = Vector(SmartMeter, ShitMeter)

  // this function will take in a MeterType and return a String
  def toString(meterType: MeterType): String = {
    allMeters.find(_.toString.toLowerCase == meterType.toString)
      .getOrElse(Unknown.toString)
      .toString
  }

  def toMeterType(str: String): MeterType = {
    allMeters.find(_.toString.toLowerCase == str)
     .getOrElse(Unknown)
  }
}