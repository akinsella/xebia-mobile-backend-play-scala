package controllers

import play.api.mvc._
import views._
import securesocial.core._
import models.web.{Menu, Header}

object HomeController extends Controller with SecureSocial {

  implicit def header[A](implicit request: SecuredRequest[A]) : Header = {
    Header( Option(request.user) )
  }

  def index = SecuredAction { implicit request =>
    Ok(
      html.Application.index( "Your new application is ready." )
    )
  }

}
