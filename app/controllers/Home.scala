package controllers

import play.api.mvc._
import models._

object Home extends Controller {

  def index = Action { request =>
    val device = Device("1983137189371987496479783")
    Ok(views.html.Home.index("Your new application is ready.", device))
  }

}