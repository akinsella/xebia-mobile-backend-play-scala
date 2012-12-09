package controllers.api

import models.notification.Device
import play.api.libs.json.Json.toJson
import play.api.mvc.{Action, Controller}
import utils.RestHelper


object DeviceService extends Controller with RestHelper {


  def options = Action(
    Ok.withHeaders(
      "Access-Control-Allow-Origin" -> "*",
      "Access-Control-Allow-Methods" -> "GET,POST,PUT,DELETE"
    )
  )

  def devices = Action(Ok(toJson(Device.all)))

  def show(id: Long) = Action {
    request => {
      Device.findById(id).map(device => {
        withLastModified(device.lastModified)(
          request.headers.get(IF_MODIFIED_SINCE).map(h => {

            val lastModifiedFromClient = fromHttpDate(h)
            val clientOutdated: Boolean = lastModifiedFromClient.isBefore(device.lastModified.getTime)

            if (clientOutdated)
              Ok(toJson(device))
            else
              NotModified

          }).getOrElse(Ok(toJson(device)))
        )

      }).getOrElse(NotFound)
    }
  }

  def create() = Action {
    request => {
      request.body.asJson.map(query => {
        Device.create(query.as[Device]).map(newId => {

          CachedEntityCreated(routes.DeviceService.show(newId), request)
        }
        ).getOrElse(NotAcceptable)
      }
      ).getOrElse(NotAcceptable)
    }
  }

  def save(id: Long) = Action {
    request => {
      request.body.asJson.map(query => {

        if (Device.findById(id).isDefined) {
          val device = query.as[Device]
          if (Device.update(id, device)) lastModifiedNow(Ok) else NotModified
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
