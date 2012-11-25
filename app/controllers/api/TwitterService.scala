package controllers.api

import play.api.mvc.{RequestHeader, Action, Controller}
import play.api.libs.json._
import com.redis.RedisClientPool
import play.Play
import models.twitter.TTTweet
import play.api.libs.ws.WS
import scala.Predef._
import scala.Left
import play.api.libs.oauth.OAuth
import scala.Right
import play.api.libs.oauth.ServiceInfo
import play.api.libs.oauth.RequestToken
import play.api.libs.oauth.OAuthCalculator
import play.api.libs.ws.WS.WSRequestHolder
import play.api.libs.oauth.ConsumerKey
import play.api.libs.json.JsObject

object TwitterService extends Controller {

  val redisClients = new RedisClientPool("localhost", 6379)

  val appKey = Play.application().configuration().getString("api.twitter.app.key")
  val appSecret = Play.application().configuration().getString("api.twitter.app.secret")

  val oauthInfosKey = "api.twitter.oauth.infos"
  val wpTweetsUrl = "http://api.twitter.com/1/statuses/user_timeline.json"

/*
  def index = Security.Authenticated(
    request => request.session.get("token"),
    _ => Results.Redirect(routes.Twitter.authenticate))(username => Action(Ok(html.index())))
*/

  def timeline = Action { request =>
    redisClients.withClient {
      redisClient => {
        val wsRequestHolder: WSRequestHolder = createUserTimelineUrl(request)
        val cacheKey = buildRequestUrl(wsRequestHolder)
        val jsonTweet = redisClient.get(cacheKey).getOrElse {
          val jsonFetched: JsValue = getAsJson(wsRequestHolder)
          println(jsonFetched)
          val tweets = jsonFetched.as[Seq[JsObject]] map { _.as[TTTweet] }
          val jsonFormatted = Json.toJson(tweets).toString()
          redisClient.set(cacheKey, jsonFormatted)
          redisClient.expire(cacheKey, 60)
          jsonFormatted
        }
        Ok( jsonTweet ).as("application/json")
      }
    }
  }

  def getAsJson(request:WSRequestHolder): JsValue = {
    Json.parse(request.get().value.get.body)
  }

  def createUserTimelineUrl(request:RequestHeader): WSRequestHolder = {
    getTokenFromRedis(request) match {
      case Right(requestToken) => WS.url(wpTweetsUrl)
        .withQueryString(
          "screen_name" -> "XebiaFR",
          "contributor_details" -> "false",
          "include_entities" -> "true",
          "include_rts" -> "true",
          "exclude_replies" -> "false",
          "count" -> "50"
        )
        .sign(OAuthCalculator(KEY, requestToken))
      case Left(e) => throw e
    }
  }

  case class TwitterUser( login: String, email: String, avatar_url: String, name: String )

  implicit def TwitterUserReads: Reads[TwitterUser] = new Reads[TwitterUser]{
    def reads(json: JsValue) =
      TwitterUser(
        (json \ "login").as[String],
        (json \ "email").as[String],
        (json \ "avatar_url").as[String],
        (json \ "name").as[String]
      )
  }

  val KEY = ConsumerKey(appKey, appSecret)

  val TWITTER = OAuth(ServiceInfo(
    "https://api.twitter.com/oauth/request_token",
    "https://api.twitter.com/oauth/access_token",
    "https://api.twitter.com/oauth/authorize", KEY),
    false)

  def authenticate = Action { request =>
    request.queryString.get("oauth_verifier").flatMap(_.headOption).map { verifier =>
      val tokenPair = getTokenFromRedis(request) match {
        case Right(requestToken) => requestToken
        case Left(e) => throw e
      }

      // We got the verifier; now get the access token, store it and back to index
      TWITTER.retrieveAccessToken(tokenPair, verifier) match {
        case Right(t) => {
          redisClients.withClient {
            redisClient => {
              redisClient.set(oauthInfosKey, Json.toJson(Map("token" -> t.token, "secret" -> t.secret)).toString())
              Redirect(controllers.routes.Home.index())
            }
          }
        }
        case Left(e) => throw e
      }
    }.getOrElse(
      TWITTER.retrieveRequestToken("http://dev.xebia.fr:9000/api/twitter/authenticate") match {
        case Right(t) => {
          // We received the unauthorized tokens in the OAuth object - store it before we proceed
          redisClients.withClient {
            redisClient => {
              redisClient.set(oauthInfosKey, Json.toJson(Map("token" -> t.token, "secret" -> t.secret)).toString())
              Redirect(TWITTER.redirectUrl(t.token))
            }
          }

        }
        case Left(e) => throw e
      })
  }

  def getTokenFromRedis(request:RequestHeader):Either[Exception, RequestToken] = {
    redisClients.withClient {
      redisClient => {
        val result :Either[Exception, RequestToken] = try {
          val json = Json.parse(redisClient.get(oauthInfosKey).getOrElse("")).as[JsObject]
          val token: RequestToken = RequestToken((json \ "token").as[String], (json \ "secret").as[String])
          Right(token)
        } catch {
          case ex:Exception => Left(ex)
        }
        result
      }
    }
  }

  def buildRequestUrl(wpPostsRequestHolder: WS.WSRequestHolder): String = {
    "%1$s?%2$s".format(wpPostsRequestHolder.url, wpPostsRequestHolder.queryString.toSeq.sorted map {
      case (key, value) => "%s=%s" format(key, value)
    } mkString ("&"))
  }

}
