package controllers.api

import cloud._
import models._
import play.api.libs.json._
import play.api.libs.ws.WS
import play.api.mvc.{Action, Controller}
import wordpress._


/**
 * Adapters for Wordpress Webservices API
 */
object WordPressService extends Controller {


  private val linkNextPage: String = """<%s>; rel="next""""
  private val linkLastPage: String = """<%s>; rel="last"""""
  private val linkFirstPage: String = """<%s>; rel="first""""
  private val linkPreviousPage: String = """<%s>; rel="prev"""""

  private val pageSize: Int = 2

  /**
   * @return authors from Xebia blogs
   */
  def authors = Action {
    CachedWSCall("http://blog.xebia.fr/wp-json-api/get_author_index/") {
      jsonFetched => (jsonFetched \ "authors").as[Seq[JsValue]] map (_.as[WPAuthor])
    }.okAsJson
  }

  /**
   * @return tags from Xebia blogs
   */
  def tags = Action {
    CachedWSCall("http://blog.xebia.fr/wp-json-api/get_tag_index/") {
      jsonFetched => (jsonFetched \ "tags").as[Seq[JsValue]] map (_.as[WPTag])
    }.okAsJson
  }


  /**
   * @return categories of posts from Xebia blogs
   */
  def categories = Action {
    CachedWSCall("http://blog.xebia.fr/wp-json-api/get_category_index/") {
      jsonFetched => (jsonFetched \ "categories").as[Seq[JsValue]] map (_.as[WPCategory])
    }.okAsJson
  }

  /**
   * @param id id of the post
   * @return tags of the post
   */
  def tagPosts(id: Long, count: Option[Int] = None, page: Option[Int]) = posts("tag", Some(id), count, page)

  /**
   * @param id id of the post
   * @return category of the post
   */
  def categoryPosts(id: Long, count: Option[Int] = None, page: Option[Int]) = posts("category", Some(id), count, page)

  /**
   * @param id id of the post
   * @return author of the post
   */
  def authorPosts(id: Long, count: Option[Int] = None, page: Option[Int]) = posts("author", Some(id), count, page)

  /**
   * @return recent posts
   */
  def recentPosts(count: Option[Int] = None, page: Option[Int]) = posts("recent", None, count, page)

  /**
   * @param _type type of entity fetched
   * @param id optional id of a post or all posts if None
   * @param count number of elements fetched
   * @param page # of the page for pagination
   * @return _type element from a post identified by id or all posts limited by count
   */
  def posts(_type: String, id: Option[Long] = None, count: Option[Int] = None, page: Option[Int]) = Action {
    implicit Request => {
      val wpPostsUrl = "http://blog.xebia.fr/wp-json-api/get_%1$s_posts/".format(_type)
      var queryStringParams = count.map(x => Seq("count" -> x.toString)).getOrElse(Seq())

      if (id.isDefined) {
        queryStringParams = queryStringParams.:+("id" -> id.get.toString)
      }

      val wpPostsRequestHolder = WS
        .url(wpPostsUrl)
        .withQueryString(queryStringParams.toArray: _*)

      val cacheKey = buildRequestUrl(wpPostsRequestHolder)

      val posts = CachedWSCall(cacheKey, wpPostsRequestHolder, Some(60)) {
        jsonFetched => (jsonFetched \ "posts").as[Seq[JsValue]] map (_.as[WPPost])
      }.get

      val numberOfPages = posts.size / pageSize

      page match {
        case None | Some(1) => {
          if (numberOfPages != 1) {

            val pageLinkHeader = List(
              linkNextPage.format(routes.WordPressService.recentPosts(count, Some(2)).absoluteURL(false)),
              linkLastPage.format(routes.WordPressService.recentPosts(count, Some(numberOfPages)).absoluteURL(false)))

            Ok(Json.toJson(posts.take(pageSize))).withHeaders(("Link", pageLinkHeader.mkString(",")))
          }
          else {
            Ok(Json.toJson(posts))
          }
        }
        case Some(`numberOfPages`) => {
          val pageLinkHeader = List(
            linkFirstPage.format(routes.WordPressService.recentPosts(count, Some(1)).absoluteURL(false)),
            linkPreviousPage.format(routes.WordPressService.recentPosts(count, Some(numberOfPages - 1)).absoluteURL(false))
          )

          Ok(Json.toJson(posts.takeRight(pageSize))).withHeaders(("Link", pageLinkHeader.mkString(",")))
        }
        case Some(x) if (x < numberOfPages) => {
          val pageLinkHeader = List(
            linkFirstPage.format(routes.WordPressService.recentPosts(count, Some(1)).absoluteURL(false)),
            linkPreviousPage.format(routes.WordPressService.recentPosts(count, Some(x - 1)).absoluteURL(false)),
            linkNextPage.format(routes.WordPressService.recentPosts(count, Some(x + 1)).absoluteURL(false)),
            linkLastPage.format(routes.WordPressService.recentPosts(count, Some(numberOfPages)).absoluteURL(false))
          )

          Ok(Json.toJson(posts.slice((x - 1) * pageSize, x * pageSize))).withHeaders(("Link", pageLinkHeader.mkString(",")))
        }
        case _ => NotFound
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
    val cacheKey: String = url + "?post_id=%s".format(id)

    val ws = WS
      .url(url)
      .withQueryString(("post_id" -> id.toString))

    CachedWSCall(cacheKey, ws) {
      jsonFetched => (jsonFetched \ "post").as[WPPost]
    }.okAsJson
  }

  private def buildRequestUrl(wpPostsRequestHolder: WS.WSRequestHolder): String = {
    "%1$s?%2$s".format(wpPostsRequestHolder.url, wpPostsRequestHolder.queryString.toSeq.sorted map {
      case (key, value) => "%s=%s" format(key, value)
    } mkString ("&"))
  }

}
