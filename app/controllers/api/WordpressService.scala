package controllers.api

import models._
import play.api.mvc.{Action, Controller}
import play.api.libs.json._
import play.api.libs.ws.WS
import wordpress._
import com.redis.{RedisClientPool, RedisClient}

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

}
