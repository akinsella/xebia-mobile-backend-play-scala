package controllers.api

import models.notification.Device
import play.api.mvc.{Action, Controller}
import play.api.libs.json.Json.toJson


object DeviceService extends Controller {

  def devices = Action {
    implicit request =>
    // Necessary if you want to run a mobile app in local browser
      if (request.method == "OPTIONS") {
        Ok.withHeaders(
          "Access-Control-Allow-Origin" -> "*",
          "Access-Control-Allow-Methods" -> "GET,POST,PUT",
          "Access-Control-Max-Age" -> "360",
          "Access-Control-Allow-Headers" -> "x-requested-with"
        )
      } else {
        val json = toJson(Device.all)
        Ok(json).as(JSON).withHeaders(
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
      case Some(d) => Ok(toJson(d)).as(JSON)
    }

  }

  def create() = Action {
    implicit request => {
      request.body.asJson match {
        case None => NotAcceptable
        case Some(query) => {
          Device.create(query.as[Device]) match {
            case Some(newId) => {
              val url = routes.DeviceService.show(newId).url
              Status(CREATED).withHeaders(LOCATION -> url)
            }
            case _ => NotModified
          }
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
          if (Device.update(id, device) > 0) {
            Ok
          } else {
            NotModified
          }
        }
        else {
          NotFound
        }
      }
    }
  }

  def delete(id: Long) = Action {
    if (Device.findById(id).isDefined) {
      if (Device.delete(id) > 0) {
        Ok
      } else {
        NotModified
      }
    }
    else {
      NotFound
    }
  }
}
