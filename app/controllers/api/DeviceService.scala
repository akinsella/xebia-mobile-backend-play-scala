package controllers.api

import models.notification.Device
import play.api.libs.json.Json.toJson
import play.api.mvc.{Action, Controller}
import utils.RestHelper


/**
 * CRUD Service for devices registred in the API (Android or iOS)
 */
object DeviceService extends Controller with RestHelper {


  /**
   *
   * @return options available for this API
   */
  def options = Action(
    Ok.withHeaders(
      "Access-Control-Allow-Origin" -> "*",
      "Access-Control-Allow-Methods" -> "GET,POST,PUT,DELETE"
    )
  )

  /**
   * @return all registered device
   */
  def devices = Action(Ok(toJson(Device.all)))


  /**
   * This call manages the IF_MODIFIED_SINCE and LAST_MODIFIED http headers
   * @param id unique identifier of a device
   * @return a device identified by its id
   */
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

  /**
   *
   * @return create a new device and give LOCATION to the resource
   */
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

  /**
   *  update a device
   * @param id identifier of the device
   * @return status of the delete process (OK, NOT_FOUND, NOT_MODIFIED or NOT_ACCEPTABLE)
   */
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

  /**
   *  delete a device
   * @param id identifier of the device
   * @return status of the delete OK, NOT_FOUND or NOT_MODIFIED)
   */
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
