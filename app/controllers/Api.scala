package controllers

import play.api.mvc.{Action, Controller}


object Api extends Controller{

  def index = Action { request =>
    Ok(views.html.api.index())
  }
}
