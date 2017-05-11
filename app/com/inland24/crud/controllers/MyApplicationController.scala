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

import play.api.mvc._
import my.samples.core.AppBindings
import play.api.libs.json.JsValue
import play.api.libs.streams.ActorFlow
import play.api.mvc.WebSocket.MessageFlowTransformer

final class MyApplicationController(bindings: AppBindings) extends Controller {

  implicit val messageFlowTransformer =
    MessageFlowTransformer.jsonMessageFlowTransformer[JsValue, JsValue]

  implicit val actorSystem = bindings.actorSystem
  implicit val materializer = bindings.materializer

  def home = Action { implicit request =>
    Ok("The API is ready")
  }

  def observable = WebSocket.accept[JsValue, JsValue] { request =>
    ActorFlow.actorRef(out => MyWebSocketActor.props(bindings.globalChannel.publishChannel, out))
  }

  def connectableObservable = WebSocket.accept[JsValue, JsValue] { request =>
    ActorFlow.actorRef(out => MyWebSocketActor.props(bindings.globalChannel.publishChannel, out))
  }
}