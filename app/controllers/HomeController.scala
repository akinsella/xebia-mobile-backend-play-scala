package controllers

import play.api.mvc._
import models.security.User

import views._
import controllers.Application.Secured

object HomeController extends Controller with Secured {

  def index = IsAuthenticated { username => _ =>
    User.findByEmail(username).map { user =>
      Ok(
        html.Application.index("Your new application is ready.", user)
      )
    }.getOrElse(Redirect("/login"))
  }

}
