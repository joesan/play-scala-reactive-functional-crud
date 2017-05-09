package com.inland24.crud.services

import com.typesafe.scalalogging.LazyLogging
import monix.execution.atomic.AtomicBoolean

object ZombieConnectorService extends LazyLogging {

  private[this] val connectionStatus = AtomicBoolean(false)

  def connect(): Unit = {
    logger.info(s"ZombieConnectorService connection status = ${connectionStatus.get}")
    connectionStatus.compareAndSet(expect = false, update = true)
  }

  def disconnect(): Unit = {
    logger.info(s"ZombieConnectorService connection status = ${connectionStatus.get}")
    connectionStatus.compareAndSet(expect = true, update = false)
  }
}