package controllers.api

import cloud._
import models._
import play.api.libs.json._
import play.api.libs.ws.WS
import play.api.mvc.{Action, Call, Controller}
import wordpress._


/**
 * Adapters for Wordpress Webservices API
 */
object WordPressService extends Controller {

  private val pageSize: Int = 10

  /**
   * @return authors from Xebia blogs
   */
  def authors = Action {
    CachedWSCall("http://blog.xebia.fr/wp-json-api/get_author_index/").mapJson {
      jsonFetched => (jsonFetched \ "authors").as[Seq[JsValue]] map (_.as[WPAuthor])
    }.fold(
      errorMessage => {
        InternalServerError(errorMessage)
      },
      response => {
        Ok(Json.toJson(response))
      }
    )
  }

  /**
   * @return tags from Xebia blogs
   */
  def tags = Action {
    CachedWSCall("http://blog.xebia.fr/wp-json-api/get_tag_index/").mapJson {
      jsonFetched => (jsonFetched \ "tags").as[Seq[JsValue]] map (_.as[WPTag])
    }.fold(
      errorMessage => {
        InternalServerError(errorMessage)
      },
      response => {
        Ok(Json.toJson(response))
      }
    )
  }


  /**
   * @return categories of posts from Xebia blogs
   */
  def categories = Action {
    CachedWSCall("http://blog.xebia.fr/wp-json-api/get_category_index/").mapJson {
      jsonFetched => (jsonFetched \ "categories").as[Seq[JsValue]] map (_.as[WPCategory])
    }.fold(
      errorMessage => {
        InternalServerError(errorMessage)
      },
      response => {
        Ok(Json.toJson(response))
      }
    )
  }

  /**
   * @param id id of the post
   * @return tags of the post
   */
  def tagPosts(id: Long, count: Option[Int] = None, page: Option[Int]) = {
    posts("tag", Some(id), count, page) {
      page => routes.WordPressService.tagPosts(id, count, Some(page))
    }
  }

  /**
   * @param id id of the post
   * @return category of the post
   */
  def categoryPosts(id: Long, count: Option[Int] = None, page: Option[Int]) = {
    posts("category", Some(id), count, page) {
      page => routes.WordPressService.categoryPosts(id, count, Some(page))
    }
  }

  /**
   * @param id id of the post
   * @return author of the post
   */
  def authorPosts(id: Long, count: Option[Int] = None, page: Option[Int]) = {
    posts("author", Some(id), count, page) {
      page => routes.WordPressService.authorPosts(id, count, Some(page))
    }
  }

  /**
   * @return recent posts
   */
  def recentPosts(count: Option[Int] = None, page: Option[Int]) = {
    posts("recent", None, count, page) {
      page => routes.WordPressService.recentPosts(count, Some(page))
    }
  }


  /**
   * @param _type type of entity fetched
   * @param id optional id of a post or all posts if None
   * @param count number of elements fetched
   * @param page # of the page for pagination
   * @param urlToPage function that generate the URL of the requested page with the page number as a function
   * @return _type element from a post identified by id or all posts limited by count
   */
  def posts(_type: String, id: Option[Long] = None, count: Option[Int] = None, page: Option[Int])(urlToPage: (Int => Call)) = Action {
    implicit Request => {
      val wpPostsUrl = "http://blog.xebia.fr/wp-json-api/get_%1$s_posts/".format(_type)
      var queryStringParams = count.map(x => Seq("count" -> x.toString)).getOrElse(Seq())

      if (id.isDefined) {
        queryStringParams = queryStringParams.:+("id" -> id.get.toString)
      }

      val wpPostsRequestHolder = WS.url(wpPostsUrl)
        .withQueryString(queryStringParams.toArray: _*)

      val responsePost = CachedWSCall(wpPostsRequestHolder, 60).mapJson {
        jsonFetched => (jsonFetched \ "posts").as[Seq[JsValue]] map (_.as[WPPost])
      }

      responsePost match {
        case Left(errorMessage) => {
          InternalServerError(errorMessage)
        }
        case Right(posts) => {
          PagedContent(posts, pageSize)(urlToPage).getPage(page.getOrElse(1))
        }
      }
    }
  }

  /**
   *
   * @param id id of the post
   * @return the post identified by its id
   */
  def showPost(id: Long) = Action {
    val url: String = "http://blog.xebia.fr/wp-json-api/get_post"

    val ws = WS
      .url(url)
      .withQueryString(("post_id" -> id.toString))

    CachedWSCall(ws).mapJson {
      jsonFetched => (jsonFetched \ "post").as[WPPost]
    }.fold(
      errorMessage => {
        InternalServerError(errorMessage)
      },
      response => {
        Ok(Json.toJson(response))
      }
    )
  }

}
