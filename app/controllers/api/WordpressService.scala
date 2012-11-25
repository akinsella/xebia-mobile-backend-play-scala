package controllers.api

import models._
import play.api.mvc.{Action, Controller}
import play.api.libs.json._
import play.api.libs.ws.WS
import com.redis.{RedisClientPool, RedisClient}
import play.api.libs.ws.WS.WSRequestHolder
import wordpress._

object WordPressService extends Controller {

  val redisClients = new RedisClientPool("localhost", 6379)

  def authors = Action {
    redisClients.withClient {
      redisClient => {

        val wpAuthorsUrl: String = "http://blog.xebia.fr/wp-json-api/get_author_index/"
        val jsonAuthors = redisClient.get(wpAuthorsUrl).getOrElse {
          val jsonFetched = Json.parse(WS.url(wpAuthorsUrl).get().value.get.body)
          val authors =  (jsonFetched \ "authors").as[Seq[JsValue]] map { _.as[WPAuthor] }
          val jsonFormatted = Json.toJson(authors).toString()
          redisClient.set(wpAuthorsUrl, jsonFormatted)
          redisClient.expire(wpAuthorsUrl, 60)
          jsonFormatted
        }
        Ok( jsonAuthors ).as("application/json")
      }
    }
  }

  def tags = Action {
    redisClients.withClient {
      redisClient => {

        val wpTagsUrl: String = "http://blog.xebia.fr/wp-json-api/get_tag_index/"
        val jsonTags = redisClient.get(wpTagsUrl).getOrElse {
          val jsonFetched = Json.parse(WS.url(wpTagsUrl).get().value.get.body)
          val tags =  (jsonFetched \ "tags").as[Seq[JsValue]] map { _.as[WPTag] }
          val jsonFormatted = Json.toJson(tags).toString()
          redisClient.set(wpTagsUrl, jsonFormatted)
          redisClient.expire(wpTagsUrl, 60)
          jsonFormatted
        }
        Ok( jsonTags ).as("application/json")
      }
    }
  }

  def categories = Action {
    redisClients.withClient {
      redisClient => {

        val wpCategoriesUrl: String = "http://blog.xebia.fr/wp-json-api/get_category_index/"
        val jsonCategories = redisClient.get(wpCategoriesUrl).getOrElse {
          val jsonFetched = Json.parse(WS.url(wpCategoriesUrl).get().value.get.body)
          val categories =  (jsonFetched \ "categories").as[Seq[JsValue]] map { _.as[WPCategory] }
          val jsonFormatted = Json.toJson(categories).toString()
          redisClient.set(wpCategoriesUrl, jsonFormatted)
          redisClient.expire(wpCategoriesUrl, 60)
          jsonFormatted
        }
        Ok( jsonCategories ).as("application/json")
      }
    }
  }

  def tagPosts(id:Long) = Action {
    posts(Some(id), "tag")
  }

  def categoryPosts(id:Long) = Action {
    posts(Some(id), "category")
  }

  def authorPosts(id:Long) = Action {
    posts(Some(id), "author")
  }

  def recentPosts = Action {
    posts(None, "recent")
  }

  def posts(id:Option[Long], _type:String, count:Long = 100) = Action {
    redisClients.withClient {
      redisClient => {
        val wpPostsUrl: String = "http://blog.xebia.fr/wp-json-api/get_%1$s_posts/".format(_type)
        var queryStringParams:Seq[(String, String)] = Seq("count" -> count.toString)

        if (id.isDefined) {
          queryStringParams = queryStringParams.:+("id" -> id.get.toString)
        }

        val wpPostsRequestHolder: WSRequestHolder = WS.url(wpPostsUrl).withQueryString(queryStringParams.toArray: _*)

        val jsonPosts = redisClient.get(wpPostsUrl).getOrElse {
          val jsonFetched = Json.parse(wpPostsRequestHolder.get().value.get.body)
          val posts = (jsonFetched \ "posts").as[Seq[JsValue]] map { _.as[WPPost] }
          val jsonFormatted = Json.toJson(posts).toString()
          redisClient.set(wpPostsUrl, jsonFormatted)
          redisClient.expire(wpPostsUrl, 60)
          jsonFormatted
        }
        Ok( jsonPosts ).as("application/json")
      }
    }
  }

}
