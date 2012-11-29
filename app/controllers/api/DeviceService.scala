package controllers.api

import models._
import models.notification.Device
import play.api.mvc.{Action, Controller}
import play.api.libs.json._
import play.api.libs.json.Json.toJson


object DeviceService extends Controller {

  def devices = Action {
    implicit request =>
    // Necessary if you want to run a mobile app in local browser
      if (request.method == "OPTIONS") {
        println("OPTIONS")
        Ok.withHeaders(
          "Access-Control-Allow-Origin" -> "*",
          "Access-Control-Allow-Methods" -> "GET,POST,PUT",
          "Access-Control-Max-Age" -> "360",
          "Access-Control-Allow-Headers" -> "x-requested-with"
        )
      } else {
        val json = toJson(Device.all)
        Ok(json).as("application/json").withHeaders(
          "Access-Control-Allow-Origin" -> "*",
          "Access-Control-Allow-Methods" -> "GET,POST",
          "Access-Control-Max-Age" -> "360",
          "Access-Control-Allow-Headers" -> "x-requested-with"
        )
      }
  }

  def show(id: Long) = Action {
    Device.findById(id) match {
      case None => NotFound
      case Some(d) => Ok(toJson(d)).as("application/json")
    }

  }

  def create() = Action {
    implicit request => {
      request.body.asJson match {
        case None => NotAcceptable
        case Some(query) => {
          Created(toJson(Device.create(query.as[Device]))).as("application/json")
        }
      }
    }
  }

  def save(id: Long) = Action {
    implicit request => request.body.asJson match {
      case None => NotAcceptable
      case Some(query) => {
        if (Device.findById(id).isDefined) {
          val device = query.as[Device]
          Device.update(device)
          println(device)
          Ok
        }
        else {
          NotFound
        }
      }
    }
  }

  def delete(id: Long) = Action {
    if (Device.findById(id).isDefined) {
      Device.delete(id)
      Ok
    }
    else {
      NotFound
    }
  }
}
