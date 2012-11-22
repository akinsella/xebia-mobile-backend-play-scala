package controllers.api

import models._
import play.api.mvc.{Action, Controller}
import play.api.libs.json._
import play.libs.WS
import wordpress.WPAuthor
import com.redis.{RedisClientPool, RedisClient}

object WordpressService extends Controller {

  val redisClients = new RedisClientPool("localhost", 6379)

  def authors = Action {
    redisClients.withClient {
      redisClient => {

        val wpAuthorsUrl: String = "http://blog.xebia.fr/wp-json-api/get_author_index/"
        val jsonAuthors = redisClient.get(wpAuthorsUrl).getOrElse {
          val json = Json.parse(WS.url(wpAuthorsUrl).get.get.getBody)
          val authors =  (json \\ "authors").head.as[Seq[JsValue]] map { _.as[WPAuthor] }
          val jsonAuthors = Json.toJson(authors).toString()
          redisClient.set(wpAuthorsUrl, jsonAuthors)
          redisClient.expire(wpAuthorsUrl, 60)
          jsonAuthors
        }
        Ok( jsonAuthors ).as("application/json")
      }
    }
  }

}
