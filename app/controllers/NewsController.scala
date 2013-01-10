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

  /**
   * News Form definition.
   */
  val newsForm: Form[News] = Form(

    // Defines a mapping that will handle News
    mapping(
      "title" -> nonEmptyText,
      "content" -> nonEmptyText,
      "imageUrl" -> nonEmptyText


    )(News.apply)(News.unapply)
  )

  /**
   * Display an empty form.
   */
  def create = Action {
    Ok(html.news.form(newsForm))
  }

  /**
   * Display a form pre-filled with an existing News.
   */
  def edit = Action {
    val existingNews = News("Some Title", "Some Content", "Some Url")
    Ok(html.news.form(newsForm.fill(existingNews)))
  }

  /**
   * Handle form submission.
   */
  def submit = Action { implicit request =>
    newsForm.bindFromRequest.fold(
      errors => BadRequest(html.news.form(errors)),
      contact => Ok(html.news.summary(news))
    )
  }

}
