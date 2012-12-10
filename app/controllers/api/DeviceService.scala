package controllers.api

import models.notification.Device
import play.api.mvc.{PlainResult, Action, Controller}
import play.api.libs.json.Json.toJson


object DeviceService extends Controller {

  private def applyHeader(result:PlainResult):PlainResult  =
    result.withHeaders(
    "Access-Control-Allow-Origin" -> "*",
    "Access-Control-Allow-Methods" -> "GET,POST,PUT,DELETE",
    "Access-Control-Max-Age" -> "360",
    "Access-Control-Allow-Headers" -> "x-requested-with")

  def options = Action {
    applyHeader(Ok)
  }

  def devices = Action {
    applyHeader(Ok(toJson(Device.all)))
  }

  def show(id: Long) = Action {
    Device.findById(id)
      .map(device => Ok(toJson(device)).as(JSON))
      .getOrElse(NotFound)
  }

  def create() = Action {
    implicit request => {
      request.body.asJson
        .map(query => {
        Device.create(query.as[Device])
          .map(newId => {
          val url = routes.DeviceService.show(newId).url
          Status(CREATED).withHeaders(LOCATION -> url)
        }).getOrElse(NotModified)
      }).getOrElse(NotAcceptable)
    }
  }

  def save(id: Long) = Action {
    implicit request => {
      request.body.asJson.map(query => {
        if (Device.findById(id).isDefined) {
          val device = query.as[Device]
          if (Device.update(id, device)) Ok else NotModified
        }
        else {
          NotFound
        }
      }).getOrElse(NotAcceptable)
    }
  }

  def delete(id: Long) = Action {
    if (Device.findById(id).isDefined) {
      if (Device.delete(id)) {
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
