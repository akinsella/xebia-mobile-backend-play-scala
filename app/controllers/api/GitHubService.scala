package controllers.api

import models._
import play.api.mvc.{Action, Controller}
import play.api.libs.json._
import play.libs.WS
import github._
import com.redis.{RedisClientPool, RedisClient}

object GitHubService extends Controller {

  val redisClients = new RedisClientPool("localhost", 6379)

  def users = Action {
    redisClients.withClient {
      redisClient => {
        val ghUsersUrl: String = "https://api.github.com/orgs/xebia-france/public_members"
        val jsonUsers = redisClient.get(ghUsersUrl).getOrElse {
          val json = Json.parse(WS.url(ghUsersUrl).get.get.getBody)
          val users = json.as[Seq[JsValue]] map { _.as[GHUser] }
          val jsonUsers = Json.toJson(users).toString()
          redisClient.set(ghUsersUrl, jsonUsers)
          redisClient.expire(ghUsersUrl, 60)
          jsonUsers
        }
        Ok( jsonUsers ).as("application/json")
      }
    }
  }

  def repositories = Action {
    redisClients.withClient {
      redisClient => {
        val ghRepositoriesUrl: String = "https://api.github.com/orgs/xebia-france/repos"
        val jsonRepositories = redisClient.get(ghRepositoriesUrl).getOrElse {
          val json = Json.parse(WS.url(ghRepositoriesUrl).get.get.getBody)
          val repositories =  json.as[Seq[JsValue]] map { _.as[GHRepository] }
          val jsonRepositories = Json.toJson(repositories).toString()
          redisClient.set(ghRepositoriesUrl, jsonRepositories)
          redisClient.expire(ghRepositoriesUrl, 60)
          jsonRepositories
        }
        Ok( jsonRepositories ).as("application/json")
      }
    }
  }

  def owners = Action {
    redisClients.withClient {
      redisClient => {
        val ghOwnersUrl: String = "https://api.github.com/orgs/xebia-france/public_members"
        val jsonOwners = redisClient.get(ghOwnersUrl).getOrElse {
          val json = Json.parse(WS.url(ghOwnersUrl).get.get.getBody)
          val owners =  json.as[Seq[JsValue]] map { _.as[GHOwner] }
          val jsonOwners = Json.toJson(owners).toString()
          redisClient.set(ghOwnersUrl, jsonOwners)
          redisClient.expire(ghOwnersUrl, 60)
          jsonOwners
        }
        Ok( jsonOwners ).as("application/json")
      }
    }
  }

}
