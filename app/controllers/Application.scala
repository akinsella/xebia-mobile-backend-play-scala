package controllers

import play.api.mvc._
import views._
import securesocial.core._

object Application extends Controller with SecureSocial {

  /**
   * Display account informations
   */
  def account = SecuredAction { implicit request =>
    Ok(
      html.Application.account(
        "Account",
        request.user
      )
    )
  }

}
