package controllers

import play.api.mvc._
import models.notification.Device

object Home extends Controller {

  def index = Action { request =>
    Ok(views.html.Home.index("Your new application is ready."))
  }

}