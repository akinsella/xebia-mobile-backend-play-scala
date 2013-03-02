package controllers

import _root_.java.text.SimpleDateFormat
import play.api.mvc._
import play.api.data._
import play.api.data.Forms._

import models.news.News
import views._

import securesocial.core._
import anorm.{NotAssigned, Id}
import models.web._
import models.web.ActionGroup
import models.web.Header
import securesocial.core.SecuredRequest
import scala.Some
import models.web.ActionItem

object NewsController extends Controller with SecureSocial {

  implicit def header[A](implicit request: SecuredRequest[A]) : Header = {
    Header(
      Option(request.user),
      None,
      Option(Sidebar(Seq(
        ActionGroup("Actions",
          Seq(
            ActionItem("news.sidebar.action.all", "/news"),
            ActionItem("news.sidebar.action.create", "/createForm")
          )
        )
      )))
    )
  }

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
      "imageUrl" -> nonEmptyText,
      "targetUrl" -> nonEmptyText,
      "draft" -> boolean,
      "publicationDate" -> nonEmptyText
    )
    {
      (title, content, imageUrl, targetUrl, draft, publicationDate) =>
        News.apply(title, content, imageUrl, targetUrl, draft, new SimpleDateFormat("yyyy-MM-dd HH:mm:ss").parse(publicationDate))
    }
    { news =>  Some(news.title, news.content, news.imageUrl, news.targetUrl, news.draft, new SimpleDateFormat("yyyy-MM-dd HH:mm:ss").format(news.publicationDate)) }
  )

  /**
   * Display an empty form.
   */
  def createForm = SecuredAction { implicit request =>
    Ok(html.News.create(request.user, None, newsForm))
  }

  /**
   * Display a form pre-filled with an existing News.
   */
  def updateForm(id: Long) = SecuredAction { implicit request =>
    News.findById(id).map { news =>
      Ok(html.News.update(request.user, Some(id), newsForm.fill(news)))
    }.getOrElse( Redirect(routes.NewsController.index()) )
  }

  /**
   * Handle form submission.
   */
  def create = SecuredAction { implicit request =>
    newsForm.bindFromRequest.fold(
      errors => BadRequest(html.News.create(request.user, None, errors)),
      news => {
        News.create(news)
        Redirect( routes.NewsController.index() )
      }
    )
  }

  /**
   * Handle form submission.
   */
  def update(id: Long) = SecuredAction { implicit request =>
    newsForm.bindFromRequest.fold(
      errors => BadRequest(html.News.update(request.user, None, errors)),
      news => {
        News.update(id, news)
        Redirect( routes.NewsController.index() )
      }
    )
  }

  /**
   * Delete a task
   */
  def delete(id: Long) = SecuredAction { implicit request =>
    News.delete(id)
    Redirect( routes.NewsController.index() )
  }

}
