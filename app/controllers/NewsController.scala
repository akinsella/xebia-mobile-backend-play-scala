package controllers

import controllers.Application.Secured

import play.api._
import play.api.mvc._
import play.api.data._
import play.api.data.Forms._

import anorm._
import models._
import models.security.User
import models.news.News
import views._

object NewsController extends Controller with Secured {

  /**
   * Display the news dashboard.
   */
  def index = IsAuthenticated { username => _ =>
    User.findByEmail(username).map { user =>
      Ok(
        views.html.News.index(
          "Index of News controller",
          News.all,
          user
        )
      )
    }.getOrElse(Forbidden)
  }

}
