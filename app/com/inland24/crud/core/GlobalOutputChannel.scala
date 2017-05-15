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

package com.inland24.crud.core

import com.inland24.crud.models.MyMessages
import monix.execution.Cancelable
import monix.reactive.OverflowStrategy.Unbounded
import monix.reactive.observers.Subscriber
import monix.reactive.subjects.ConcurrentSubject


class GlobalOutputChannel {
  implicit val s = monix.execution.Scheduler.Implicits.global
  val publishChannel: ConcurrentSubject[MyMessages, MyMessages] = ConcurrentSubject.publish[MyMessages](Unbounded)

  def unsafeSubscribeFn(subscriber: Subscriber[MyMessages]): Cancelable =
    publishChannel.subscribe(subscriber)
}
object GlobalOutputChannel {

  def apply = new GlobalOutputChannel
}