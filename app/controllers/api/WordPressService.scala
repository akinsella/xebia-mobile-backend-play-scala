package controllers.api

import cloud.CachedWSCall
import play.api.libs.ws.WS
import play.api.mvc.{Action, Controller}
import models.wordpress._
import play.api.libs.json._

import play.api.Logger

import play.libs.Akka

import akka.actor._
import scala.concurrent.duration._
import play.api.libs.concurrent.Execution.Implicits._
import models.wordpress.stripped.WPPosts
import scala.Some
import play.api.mvc.Call
import cloud.PagedContent


/**
 * Adapters for Wordpress Webservices API
 */
object WordPressService extends Controller {

  implicit val timeout: Long = 15000

  /**
   * @return authors from Xebia blogs
   */
  def dates = Action {
    CachedWSCall("http://blog.xebia.fr/wp-json-api/get_date_index/")
      .mapJson { jsonFetched =>
        val jsArray: JsArray = jsonFetched.as[JsArray](WPYear.WPYearSeqRead)
        println(jsArray.toString())
        jsArray.as[Seq[WPYear]]
      }
      .fold(
        errorMessage => {
          InternalServerError(errorMessage)
        },
        response => {
          Ok(Json.toJson(response))
        }
      )
  }

  /**
   * @return authors from Xebia blogs
   */
  def authors = Action {
    CachedWSCall("http://blog.xebia.fr/wp-json-api/get_author_index/")
      .mapJson {
      jsonFetched => (jsonFetched \ "authors").as[Seq[JsValue]] map (_.as[WPAuthor])
    }
      .fold(
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
    CachedWSCall("http://blog.xebia.fr/wp-json-api/get_tag_index/")
      .mapJson {
        jsonFetched => (jsonFetched \ "tags").as[Seq[JsValue]] map (_.as[WPTag])
      }
      .fold(
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
    CachedWSCall("http://blog.xebia.fr/wp-json-api/get_category_index/")
      .mapJson {
        jsonFetched => (jsonFetched \ "categories").as[Seq[JsValue]] map (_.as[WPCategory])
      }
      .fold(
        errorMessage => {
          InternalServerError(errorMessage)
        },
        response => {
          Ok(Json.toJson(response))
        }
      )
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
        Ok(Json.toJson(response)(WPPost.WPPostFormat))
      }
    )
  }

  /**
   * @param id id of the post
   * @return tags of the post
   */
  def tagPosts(id: Long, count: Option[Int] = None, page: Option[Int]) = {
    posts("tag", Some(id), count, page, WPPosts.WPPostsFormat) { page =>
      routes.WordPressService.tagPosts(id, count, Some(page))
    }
  }

  /**
   * @param id id of the post
   * @return category of the post
   */
  def categoryPosts(id: Long, count: Option[Int] = None, page: Option[Int]) = {
    posts("category", Some(id), count, page, WPPosts.WPPostsFormat) { page =>
        routes.WordPressService.categoryPosts(id, count, Some(page))
    }
  }

  /**
   * @param id id of the post
   * @return author of the post
   */
  def authorPosts(id: Long, count: Option[Int] = None, page: Option[Int]) = {
    posts("author", Some(id), count, page, WPPosts.WPPostsFormat) { page =>
        routes.WordPressService.authorPosts(id, count, Some(page))
    }
  }

  /**
   * @return recent posts
   */
  def recentPosts(count: Option[Int] = None, page: Option[Int]) = {
    posts("recent", None, count, page, WPPosts.WPPostsFormat) { page =>
      routes.WordPressService.recentPosts(count, Some(page))
    }
  }

  /**
   * @return recent posts ids
   */
  def datePosts(year: Int, month: Int) = {
    postsByDate(year, month, WPPosts.WPPostsFormat)
  }

  /**
   * @param _type type of entity fetched
   * @param id optional id of a post or all posts if None
   * @param count number of elements fetched
   * @param page # of the page for pagination
   * @param postWriter how to transform the wp post to JSON format for output
   * @param urlToPage function that generate the URL of the requested page with the page number as a function
   * @return _type element from a post identified by id or all posts limited by count
   */
  def posts(_type: String, id: Option[Long] = None, count: Option[Int] = None, page: Option[Int], postWriter: Writes[WPPosts])(urlToPage: (Int => Call)) = Action { implicit Request =>
    val wpPostsUrl = "http://blog.xebia.fr/wp-json-api/get_%1$s_posts_sync_data/".format(_type)
    var queryStringParams = count.map(x => Seq("count" -> x.toString)).getOrElse(Seq())

    if (id.isDefined) {
      queryStringParams = queryStringParams.:+("id" -> id.get.toString)
    }

    val currentPage: Int = page.getOrElse(1)
    queryStringParams = queryStringParams.:+("page" -> currentPage.toString)

    val wpPostsRequestHolder = WS.url(wpPostsUrl)
      .withQueryString(queryStringParams.toArray: _*)

    val responsePost = CachedWSCall(wpPostsRequestHolder, 60).mapJson {
      jsonFetched => (jsonFetched).as[WPPosts]
    }

    responsePost
      .fold(
        message => InternalServerError(message),
        response => {
          val links = PagedContent(response.count, response.pages, currentPage)(urlToPage).getHeader
          Ok(Json.toJson(response)(postWriter)).withHeaders(links)

        }
      )
  }


  /**
   * @param year year of elements fetched
   * @param month month of elements fetched
   * @param postWriter how to transform the wp post to JSON format for output
   * @return _type element from a post identified by id or all posts limited by count
   */
  def postsByDate(year: Int, month: Int, postWriter: Writes[WPPosts]) = Action { implicit Request =>
    val wpPostsUrl = "http://blog.xebia.fr/wp-json-api/get_date_posts_sync_data/"
    val queryStringParams = Seq("date" -> "%1$s%2$02d".format(year, month), "count" -> 1000.toString)

    val wpPostsRequestHolder = WS.url(wpPostsUrl).withQueryString(queryStringParams.toArray: _*)

    val responsePost = CachedWSCall(wpPostsRequestHolder, 60).mapJson {
      jsonFetched => (jsonFetched).as[WPPosts]
    }

    responsePost
      .fold(
      message => InternalServerError(message),
      response => {
        Ok(Json.toJson(response)(postWriter))

      }
    )
  }


  // TODO = Is it needed ?
  def synchronize = Action {

    // say hello
    Logger.info("hello, synchronize action started")

    val Tags = "tags"
    val Categories = "categories"
    val Authors = "authors"
    val Posts = "posts"

    val synchronizationActor = Akka.system.actorOf(Props(new Actor {
      def receive = {
        case Tags => synchronizeTags()
        case Categories => synchronizeCategories()
        case Authors => synchronizeAuthors()
      }
    }))

    // Repeat every 5 seconds, start 5 seconds after start
    Akka.system.scheduler.schedule( 10 seconds, 1 hour, synchronizationActor, Tags)
    Akka.system.scheduler.schedule( 20 seconds, 1 hour, synchronizationActor, Categories)
    Akka.system.scheduler.schedule( 30 seconds, 1 hour, synchronizationActor, Authors)

    Ok("Your new application is ready.")
  }

  def synchronizeTags() {
    CachedWSCall("http://blog.xebia.fr/wp-json-api/get_tag_index/").mapJson {
      jsonFetched => (jsonFetched \ "tags").as[Seq[JsValue]] map (_.as[WPTag])
    }.fold(
      errorMessage => {
        println("Got some error on tags synchronization")
      },
      response => {
        println("Tags synchronized")
      }
    )
  }

  def synchronizeAuthors() {
    CachedWSCall("http://blog.xebia.fr/wp-json-api/get_author_index/").mapJson {
      jsonFetched => (jsonFetched \ "authors").as[Seq[JsValue]] map (_.as[WPAuthor])
    }.fold(
      errorMessage => {
        println("Got some error on authors synchronization")
      },
      response => {
        println("Authors synchronized")
      }
    )
  }

  def synchronizeCategories() {
    CachedWSCall("http://blog.xebia.fr/wp-json-api/get_category_index/").mapJson {
      jsonFetched => (jsonFetched \ "categories").as[Seq[JsValue]] map (_.as[WPCategory])
    }.fold(
      errorMessage => {
        println("Got some error on categories synchronization")
      },
      response => {
        println("Categories synchronized")
      }
    )
  }

}
