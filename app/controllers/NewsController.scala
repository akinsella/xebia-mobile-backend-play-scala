package controllers

import play.api._
import play.api.mvc._
import play.api.data._
import play.api.data.Forms._

import anorm._
import models._
import models.news.News
import views._

import securesocial.core._

object NewsController extends Controller with SecureSocial {

  /**
   * Display the news dashboard.
   */
  def index = SecuredAction { implicit request =>
    Ok(
      html.News.index(
        "Index of News controller",
        News.all,
        request.user
      )
    )
  }

}
