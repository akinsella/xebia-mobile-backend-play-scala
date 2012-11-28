package controllers

import play.api.mvc._
import models.notification.Device

object Home extends Controller {

  def index = Action { request =>
    val device = Device("1983137189371987496479783", "ZJKLR249035UR3RNLJFEZHTIZT4RE0Z")
    Ok(views.html.Home.index("Your new application is ready.", device))
  }

}