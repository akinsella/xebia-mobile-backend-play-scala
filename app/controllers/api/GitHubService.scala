package controllers.api

import models.github._
import play.api.mvc.{Action, Controller}
import play.api.libs.json._
import play.api.libs.ws.WS

import com.redis._
import cloud.{Connectivity, CloudFoundry}

object GitHubService extends Controller {

  def users = Action {
    Connectivity.withRedisClient {
      redisClient => {
        val ghUsersUrl: String = "https://api.github.com/orgs/xebia-france/public_members"
        val jsonUsers = redisClient.get(ghUsersUrl).getOrElse {
          val jsonFetched = Json.parse(WS.url(ghUsersUrl).get().value.get.body)
          val users = jsonFetched.as[Seq[JsValue]] map { _.as[GHUser] }
          val jsonFormatted = Json.toJson(users).toString()
          redisClient.set(ghUsersUrl, jsonFormatted)
          redisClient.expire(ghUsersUrl, 60)
          jsonFormatted
        }
        Ok( jsonUsers ).as("application/json")
      }
    }
  }

  def repositories = Action {
    Connectivity.withRedisClient {
      redisClient => {
        val ghRepositoriesUrl: String = "https://api.github.com/orgs/xebia-france/repos"
        val jsonRepositories = redisClient.get(ghRepositoriesUrl).getOrElse {
          val jsonFetched = Json.parse(WS.url(ghRepositoriesUrl).get().value.get.body)
          val repositories =  jsonFetched.as[Seq[JsValue]] map { _.as[GHRepository] }
          val jsonFormatted = Json.toJson(repositories).toString()
          redisClient.set(ghRepositoriesUrl, jsonFormatted)
          redisClient.expire(ghRepositoriesUrl, 60)
          jsonFormatted
        }
        Ok( jsonRepositories ).as("application/json")
      }
    }
  }

  def owners = Action {
    Connectivity.withRedisClient {
      redisClient => {
        val ghOwnersUrl: String = "https://api.github.com/orgs/xebia-france/public_members"
        val jsonOwners = redisClient.get(ghOwnersUrl).getOrElse {
          val jsonFetched = Json.parse(WS.url(ghOwnersUrl).get().value.get.body)
          val owners =  jsonFetched.as[Seq[JsValue]] map { _.as[GHOwner] }
          val jsonFormatted = Json.toJson(owners).toString()
          redisClient.set(ghOwnersUrl, jsonFormatted)
          redisClient.expire(ghOwnersUrl, 60)
          jsonFormatted
        }
        Ok( jsonOwners ).as("application/json")
      }
    }
  }

}
