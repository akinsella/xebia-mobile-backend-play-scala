package controllers.api

import cloud._
import models._
import play.api.libs.json._
import play.api.libs.ws.WS
import play.api.mvc.{ Action, Controller}
import wordpress._


/**
 * Adapters for Wordpress Webservices API
 */
object WordPressService extends Controller {


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
  def tagPosts(id: Long, count: Option[Int] = None) = posts("tag", Some(id), count)

  /**
   * @param id id of the post
   * @return category of the post
   */
  def categoryPosts(id: Long, count: Option[Int] = None) = posts("category", Some(id), count)

  /**
   * @param id id of the post
   * @return author of the post
   */
  def authorPosts(id: Long, count: Option[Int] = None) = posts("author", Some(id), count)

  /**
   * @return recent posts
   */
  def recentPosts(count: Option[Int] = None) = posts("recent", None, count)

  /**
   * @param _type type of entity fetched
   * @param id optional id of a post or all posts if None
   * @param count number of elements fetched
   * @return _type element from a post identified by id or all posts limited by count
   */
  def posts(_type: String, id: Option[Long] = None, count: Option[Int] = None) = Action {
    val wpPostsUrl = "http://blog.xebia.fr/wp-json-api/get_%1$s_posts/".format(_type)
    var queryStringParams = count.map(x => Seq("count" -> x.toString)).getOrElse(Seq())

    if (id.isDefined) {
      queryStringParams = queryStringParams.:+("id" -> id.get.toString)
    }

    val wpPostsRequestHolder = WS
      .url(wpPostsUrl)
      .withQueryString(queryStringParams.toArray: _*)

    val cacheKey = buildRequestUrl(wpPostsRequestHolder)

    CachedWSCall(cacheKey, wpPostsRequestHolder, Some(60)) {
      jsonFetched => (jsonFetched \ "posts").as[Seq[JsValue]] map (_.as[WPPost])
    }.okAsJson
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
