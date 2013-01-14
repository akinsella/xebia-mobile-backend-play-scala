package controllers

import play.api.mvc._
import views._
import securesocial.core._

object HomeController extends Controller with SecureSocial {

  def index = SecuredAction { implicit request =>
    Ok(
      html.Application.index(
        "Your new application is ready.", request.user
      )
    )
  }


}
