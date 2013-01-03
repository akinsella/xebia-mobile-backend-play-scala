package controllers.api

import cloud.{CachedWSCall, Connectivity}
import models.twitter.TTTweet
import play.Play
import play.api.Play.current
import play.api.cache.Cache
import play.api.libs.json._
import play.api.libs.oauth.ConsumerKey
import play.api.libs.oauth.OAuth
import play.api.libs.oauth.OAuthCalculator
import play.api.libs.oauth.RequestToken
import play.api.libs.oauth.ServiceInfo
import play.api.libs.ws.WS
import play.api.libs.ws.WS.WSRequestHolder
import play.api.mvc.{RequestHeader, Action, Controller}
import scala.Left
import scala.Predef._
import scala.Right

/**
 * * Adapters for Twitter  Webservices API
 */
object TwitterService extends Controller {

  private val appKey = Play.application().configuration().getString("api.twitter.app.key")
  private val appSecret = Play.application().configuration().getString("api.twitter.app.secret")
  private val appBasePath = Play.application().configuration().getString("app.base.path")

  private val oauthInfosKey = "api.twitter.oauth.infos"
  private val wpTweetsUrl = "http://api.twitter.com/1/statuses/user_timeline.json"

  private val KEY = ConsumerKey(appKey, appSecret)

  private val TWITTER = OAuth(ServiceInfo(
    "https://api.twitter.com/oauth/request_token",
    "https://api.twitter.com/oauth/access_token",
    "https://api.twitter.com/oauth/authorize", KEY),
    false)


  /**
   * @return twitter timeline of XebiaFR User
   */
  def timeline = Action {
    request => {
      val wsRequestHolder: WSRequestHolder = createUserTimelineUrl(request)

      play.Logger.debug("Twitter tiemline resource url : %s".format(wsRequestHolder))
      Ok {
        Json.toJson(
          CachedWSCall(wsRequestHolder).mapJson {
            jsonFetched => jsonFetched.as[Seq[JsObject]] map (_.as[TTTweet])
          }
        )
      }
    }
  }

  private def buildRequestUrl(wpPostsRequestHolder: WS.WSRequestHolder): String = {
    "%1$s?%2$s".format(wpPostsRequestHolder.url, wpPostsRequestHolder.queryString.toSeq.sorted map {
      case (key, value) => "%s=%s" format(key, value)
    } mkString ("&"))
  }

  private def createUserTimelineUrl(request: RequestHeader): WSRequestHolder = {
    getTokenFromRedis(request) match {
      case Right(requestToken) => WS.url(wpTweetsUrl)
        .withQueryString(
        "screen_name" -> "XebiaFR",
        "contributor_details" -> "false",
        "include_entities" -> "true",
        "include_rts" -> "true",
        "exclude_replies" -> "false",
        "count" -> "50"
      ).sign(OAuthCalculator(KEY, requestToken))
      case Left(e) => throw e
    }
  }

  private def getTokenFromRedis(request: RequestHeader): Either[Exception, RequestToken] = {
    val result: Either[Exception, RequestToken] = try {
      val json = Json.parse(Cache.getOrElse(oauthInfosKey)("")).as[JsObject]
      val token: RequestToken = RequestToken((json \ "token").as[String], (json \ "secret").as[String])
      Right(token)
    } catch {
      case ex: Exception => Left(ex)
    }
    result
  }

  def authenticate = Action {
    request =>
      val twitterUrl: String = "%s/api/twitter/authenticate".format(appBasePath)
      play.Logger.info("Twitter url : %s".format(twitterUrl))
      request.queryString.get("oauth_verifier").flatMap(_.headOption).map {
        verifier =>
          val tokenPair = getTokenFromRedis(request) match {
            case Right(requestToken) => requestToken
            case Left(e) => throw e
          }

          // We got the verifier; now get the access token, store it and back to index
          TWITTER.retrieveAccessToken(tokenPair, verifier) match {
            case Right(t) => {
              Connectivity.withRedisClient {
                redisClient => {
                  redisClient.set(oauthInfosKey, Json.toJson(Map("token" -> t.token, "secret" -> t.secret)).toString())
                  Redirect(controllers.routes.Home.index())
                }
              }
            }
            case Left(e) => throw e
          }
      }.getOrElse(
        TWITTER.retrieveRequestToken(twitterUrl) match {
          case Right(t) => {
            // We received the unauthorized tokens in the OAuth object - store it before we proceed
            Connectivity.withRedisClient {
              redisClient => {
                redisClient.set(oauthInfosKey, Json.toJson(Map("token" -> t.token, "secret" -> t.secret)).toString())
                Redirect(TWITTER.redirectUrl(t.token))
              }
            }

          }
          case Left(e) => {
            throw e
          }
        })
  }


}
