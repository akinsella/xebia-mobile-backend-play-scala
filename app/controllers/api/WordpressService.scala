package controllers.api

import models._
import play.api.mvc.{Action, Controller}
import play.api.libs.json._
import play.libs.WS
import wordpress._
import com.redis.{RedisClientPool, RedisClient}

object WordPressService extends Controller {

  val redisClients = new RedisClientPool("localhost", 6379)

  def authors = Action {
    redisClients.withClient {
      redisClient => {

        val wpAuthorsUrl: String = "http://blog.xebia.fr/wp-json-api/get_author_index/"
        val jsonAuthors = redisClient.get(wpAuthorsUrl).getOrElse {
          val json = Json.parse(WS.url(wpAuthorsUrl).get.get.getBody)
          val authors =  (json \ "authors").as[Seq[JsValue]] map { _.as[WPAuthor] }
          val jsonAuthors = Json.toJson(authors).toString()
          redisClient.set(wpAuthorsUrl, jsonAuthors)
          redisClient.expire(wpAuthorsUrl, 60)
          jsonAuthors
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
          val json = Json.parse(WS.url(wpTagsUrl).get.get.getBody)
          val tags =  (json \ "tags").as[Seq[JsValue]] map { _.as[WPTag] }
          val jsonTags = Json.toJson(tags).toString()
          redisClient.set(wpTagsUrl, jsonTags)
          redisClient.expire(wpTagsUrl, 60)
          jsonTags
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
          val json = Json.parse(WS.url(wpCategoriesUrl).get.get.getBody)
          val categories =  (json \ "categories").as[Seq[JsValue]] map { _.as[WPCategory] }
          val jsonCategories = Json.toJson(categories).toString()
          redisClient.set(wpCategoriesUrl, jsonCategories)
          redisClient.expire(wpCategoriesUrl, 60)
          jsonCategories
        }
        Ok( jsonCategories ).as("application/json")
      }
    }
  }

}
