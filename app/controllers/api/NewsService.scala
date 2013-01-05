package controllers.api

import play.api.libs.json.Json.toJson
import play.api.mvc.{Action, Controller}
import utils.RestHelper
import models.news.News


/**
 * CRUD Service for news
 */
object NewsService extends Controller with RestHelper {

  /**
   *
   * @return options available for this API
   */
  def options = Action(
    Ok.withHeaders(
      "Access-Control-Allow-Origin" -> "*",
      "Access-Control-Allow-Methods" -> "GET,POST,PUT,DELETE"
    )
  )

  /**
   * @return all news
   */
  def all = Action(Ok(toJson(News.all)))


  /**
   * This call manages the IF_MODIFIED_SINCE and LAST_MODIFIED http headers
   * @param id unique identifier of a news
   * @return a news identified by its id
   */
  def show(id: Long) = Action {
    request => {
      News.findById(id).map(news => {
        withLastModified(news.lastModified)(
          request.headers.get(IF_MODIFIED_SINCE).map(h => {

            val lastModifiedFromClient = fromHttpDate(h)
            val clientOutdated: Boolean = lastModifiedFromClient.isBefore(news.lastModified.getTime)

            if (clientOutdated)
              Ok(toJson(news))
            else
              NotModified

          }).getOrElse(Ok(toJson(news)))
        )

      }).getOrElse(NotFound)
    }
  }

  /**
   *
   * @return create a news and give LOCATION to the resource
   */
  def create() = Action {
    request => {
      request.body.asJson.map(query => {
        News.create(query.as[News]).map(newId => {

          CachedEntityCreated(routes.NewsService.show(newId), request)
        }
        ).getOrElse(NotAcceptable)
      }
      ).getOrElse(NotAcceptable)
    }
  }

  /**
   *  update a news
   * @param id identifier of the news
   * @return status of the delete process (OK, NOT_FOUND, NOT_MODIFIED or NOT_ACCEPTABLE)
   */
  def save(id: Long) = Action {
    request => {
      request.body.asJson.map(query => {

        if (News.findById(id).isDefined) {
          val news = query.as[News]
          if (News.update(id, news)) lastModifiedNow(Ok) else NotModified
        }
        else {
          NotFound
        }
      }).getOrElse(NotAcceptable)
    }
  }

  /**
   *  delete a news
   * @param id identifier of the news
   * @return status of the delete OK, NOT_FOUND or NOT_MODIFIED)
   */
  def delete(id: Long) = Action {
    if (News.findById(id).isDefined) {
      if (News.delete(id)) {
        Ok
      } else {
        NotModified
      }
    }
    else {
      NotFound
    }
  }
}
