package controllers

import play.api.mvc._
import views._
import securesocial.core._
import models.web.{Menu, Header}

object Application extends Controller with SecureSocial {

  implicit def header[A](implicit request: SecuredRequest[A]) : Header = {
    Header( Option(request.user) )
  }

  /**
   * Display account informations
   */
  def account = SecuredAction { implicit request =>
    Ok(
      html.Application.account("Account")
    )
  }

}
