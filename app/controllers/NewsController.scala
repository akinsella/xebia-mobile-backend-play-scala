package controllers

import play.api.mvc._
import play.api.data._
import play.api.data.Forms._

import models.news.News
import views._

import securesocial.core._
import anorm.{NotAssigned, Id}

object NewsController extends Controller with SecureSocial {

  /**
   * Display the news dashboard.
   */
  def index = SecuredAction { implicit request =>
    Ok(
      html.News.index(
        "Index of News controller",
        request.user,
        News.all
      )
    )
  }

  /**
   * News Form definition.
   */
  val newsForm: Form[News] = Form(
    mapping(
      "title" -> nonEmptyText,
      "content" -> nonEmptyText,
      "imageUrl" -> nonEmptyText
    )
    {
      (title, content, imageUrl) => News.apply(title, content, imageUrl)
    }
    {
      news =>  Some(news.title, news.content, news.imageUrl)
    }
  )

  /**
   * Display an empty form.
   */
  def create = SecuredAction { implicit request =>
    Ok(html.News.form(request.user, None, newsForm))
  }

  /**
   * Display a form pre-filled with an existing News.
   */
  def edit(newsId: Long) = SecuredAction { implicit request =>
    News.findById(newsId).map { news =>
      Ok(html.News.form(request.user, Some(newsId), newsForm.fill(news)))
    }.getOrElse( Redirect(routes.NewsController.index()) )
  }

  /**
   * Handle form submission.
   */
  def submit = SecuredAction { implicit request =>
    newsForm.bindFromRequest.fold(
      errors => BadRequest(html.News.form(request.user, None, errors)),
      news => {
        News.create(news)
        Redirect(routes.NewsController.index())
      }
    )
  }

  /**
   * Delete a task
   */
  def delete(newsId: Long) = SecuredAction { implicit request =>
    News.delete(newsId)
    Redirect(routes.NewsController.index())
  }

}
