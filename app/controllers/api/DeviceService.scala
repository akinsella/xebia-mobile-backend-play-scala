package controllers.api

import models._
import play.api.mvc.{Action, Controller}
import play.api.libs.json.Json.toJson
import play.api.libs.json.Json
import com.wordnik.swagger.annotations._

@Api(value = "/api/device", description = "Operations about devices", listingPath = "/api-docs.{format}/device")
object DeviceService extends Controller {

  @ApiOperation(value = "Show all devices", httpMethod = "GET")
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

  @ApiOperation(value = "Find one device by id", httpMethod = "GET")
  def show(@ApiParam(name = "id", value = "Internal id of the device", required = true) id: Long) = Action {
    Ok(toJson(Device.findById(id))).as("application/json")
  }

  @ApiOperation(value = "Create a new device", httpMethod = "PUT")
  @ApiParamsImplicit(Array(
    new ApiParamImplicit(value = "Add a new device in the database", required = true, dataType = "Device", paramType = "body")
  ))
  def create() = Action {
    implicit request => {
      request.body.asJson match {
        case None => NotAcceptable
        case Some(query) => {
          Device.create(query.as[Device])
          Created
        }
      }
    }
  }

  @ApiOperation(value = "Update an existing device", httpMethod = "POST")
  @ApiErrors(Array(
    new ApiError(code = 404, reason = "Device not found")))
  def save(@ApiParam(name = "id", value = "Internal id of the device", required = true) id: Long) = Action {
    implicit request => request.body.asText match {
      case None => NotAcceptable
      case Some(query) => {
        if (Device.findById(id).isDefined) {
          val device = Json.parse(query).as[Device]
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

  @ApiOperation(value = "Delete an existing device", httpMethod = "DELETE")
  @ApiErrors(Array(
    new ApiError(code = 404, reason = "Device not found")))
  def delete(@ApiParam(name = "id", value = "Internal id of the device", required = true) id: Long) = Action {
    implicit request => request.body.asText match {
      case None => NotAcceptable
      case Some(query) => {
        if (Device.findById(id).isDefined) {
          Device.delete(id)
          Ok
        }
        else {
          NotFound
        }
      }
    }
  }
}