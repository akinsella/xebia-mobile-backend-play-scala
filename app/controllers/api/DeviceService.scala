package controllers.api

import models._
import notification.Device
import play.api.mvc.{Action, Controller}
import play.api.libs.json.Json.toJson

import com.wordnik.swagger.annotations._

@Api(value = "/api/device", description = "Operations about devices", listingPath = "/api-docs.{format}/device")
object DeviceService extends Controller {

  @ApiOperation(value = "Get all devices", httpMethod = "GET", responseClass = "models.notification.Device", multiValueResponse = true)
  def devices = Action {
    implicit request =>
    // Necessary if you want to run a mobile app in local browser
      if (request.method == "OPTIONS") {
        println("OPTIONS")
        Ok.withHeaders(
          "Access-Control-Allow-Origin" -> "*",
          "Access-Control-Allow-Methods" -> "GET,POST,PUT",
          "Access-Control-Max-Age" -> "1",
          "Access-Control-Allow-Headers" -> "x-requested-with,content-type"
        )
      } else {
        val json = toJson(Device.all)
        Ok(json).as("application/json").withHeaders(
          "Access-Control-Allow-Origin" -> "*",
          "Access-Control-Allow-Methods" -> "GET,POST,PUT",
          "Access-Control-Max-Age" -> "360",
          "Access-Control-Allow-Headers" -> "x-requested-with"
        )
      }
  }

  @ApiOperation(value = "Find one device by id", httpMethod = "GET", responseClass = "models.notification.Device")
  @ApiParamsImplicit(Array(
    new ApiParamImplicit(name = "id", required = true, dataType = "Long", paramType = "path")
  ))
  def show(id: Long) = Action {
    Device.findById(id) match {
      case None => NotFound
      case Some(d) => Ok(toJson(d)).as("application/json")
    }
  }

  @ApiOperation(value = "Create a new device", httpMethod = "POST", responseClass = "models.notification.Device")
  @ApiParamsImplicit(Array(
    new ApiParamImplicit(name = "Add a new device in the database", required = true, dataType = "models.notification.Device", paramType = "body")
  ))
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

  @ApiOperation(value = "Update an existing device", httpMethod = "PUT")
  @ApiErrors(Array(
    new ApiError(code = 404, reason = "Device not found")))
  @ApiParamsImplicit(Array(
    new ApiParamImplicit(name = "device", required = true, dataType = "models.notification.Device", paramType = "body"),
    new ApiParamImplicit(name = "id", required = true, dataType = "Long", paramType = "path")
  ))
  def save(id: Long) = Action {
    implicit request => request.body.asJson match {
      case None => NotAcceptable
      case Some(query) => {
        if (Device.findById(id).isDefined) {
          val device = query.as[Device]
          Device.update(device)
          Ok
        }
        else {
          NotFound
        }
      }
    }
  }

  @ApiOperation(value = "Delete an existing device", httpMethod = "DELETE")
  @ApiErrors(Array(
    new ApiError(code = 404, reason = "Device not found")))
  @ApiParamsImplicit(Array(
    new ApiParamImplicit(name = "id", required = true, dataType = "Long", paramType = "path")
  ))
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
