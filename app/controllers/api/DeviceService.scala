package controllers.api

import models._
import models.notification.Device
import play.api.mvc.{Action, Controller}
import play.api.libs.json._

object DeviceService extends Controller  {

  def devices = Action { implicit request =>
    // Necessary if you want to run a mobile app in local browser
    if (request.method == "OPTIONS") {
      println("OPTIONS")
      Ok.withHeaders(
        "Access-Control-Allow-Origin" -> "*",
        "Access-Control-Allow-Methods" -> "GET,POST",
        "Access-Control-Max-Age" -> "360",
        "Access-Control-Allow-Headers" -> "x-requested-with"
      )
    } else {
      val json = Json.toJson(Device.all)
      Ok(json).as("application/json").withHeaders(
        "Access-Control-Allow-Origin" -> "*",
        "Access-Control-Allow-Methods" -> "GET,POST",
        "Access-Control-Max-Age" -> "360",
        "Access-Control-Allow-Headers" -> "x-requested-with"
      )
    }
  }

  def show(id: Long) = Action {
    Ok(Json.toJson(Device.findById(Some(id)))).as("application/json")
  }

  def create() = Action { implicit request =>
    val device = Json.parse(request.body.asText.get).as[Device]
    println(device)
    Created
  }

  def save(id: Option[Long]) = Action { implicit request =>
    val device = Json.parse(request.body.asText.get).as[Device]
    println(device)
    Ok
  }

  def delete(id: Long) = Action {
    println("deleting {id}", id)
    Device.delete(id)
    Ok
  }
}