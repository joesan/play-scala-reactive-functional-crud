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

package my.samples.controllers

import akka.actor.{ Actor, ActorRef, PoisonPill, Props }
import com.typesafe.scalalogging.LazyLogging
import monix.execution.Ack.Continue
import monix.execution.cancelables.SingleAssignmentCancelable
import monix.execution.{ Ack, Scheduler }
import monix.reactive.observers.Subscriber
import monix.reactive.subjects.ConcurrentSubject
import my.samples.models.MyMessages
import org.joda.time.DateTime
import play.api.libs.json._

import scala.concurrent.Future

class MyWebSocketActor(producer: ConcurrentSubject[MyMessages, MyMessages], consumer: ActorRef)(implicit s: Scheduler) extends Actor with LazyLogging {

  //private[this] val logger = Logger
  private[this] val cancelable = SingleAssignmentCancelable()

  override def preStart = {
    // 1. transform the messages in the producer to a JSON
    val source = producer.map(elem => Json.toJson(elem))

    // 2. create a subscriber and pipe the messages to the actor
    val subscriber = new Subscriber[JsValue] {
      override implicit def scheduler: Scheduler = s

      override def onError(ex: Throwable): Unit = {
        logger.warn(s"Error while serving a web-socket stream", ex)
        consumer ! Json.obj(
          "event" -> "error",
          "type" -> ex.getClass.getName,
          "message" -> ex.getMessage,
          "timestamp" -> DateTime.now()
        )

        self ! PoisonPill
      }

      override def onComplete(): Unit = {
        consumer ! Json.obj("event" -> "complete", "timestamp" -> DateTime.now())
        self ! PoisonPill
      }

      override def onNext(elem: JsValue): Future[Ack] = {
        println("in the onNext")
        logger.info(s"received event ${elem.toString}")
        consumer ! elem
        Continue
      }
    }

    // 3. subscribe the << subscriber >> to the source
    cancelable := source.subscribe(subscriber)
  }

  override def postStop = {
    cancelable.cancel()
  }

  def receive = {
    case msg: String =>
      consumer ! ("I received your message: " + msg)
  }
}

object MyWebSocketActor {
  implicit val s = monix.execution.Scheduler.Implicits.global
  def props(producer: ConcurrentSubject[MyMessages, MyMessages], consumer: ActorRef) =
    {
      println("in the WebSocket Actor")
      Props(new MyWebSocketActor(producer, consumer))
    }
}