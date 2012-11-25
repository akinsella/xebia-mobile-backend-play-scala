package controllers.api

import models._
import eventbrite.EBEvent
import play.api.mvc.{Action, Controller}
import play.api.libs.json._
import play.api.libs.ws.WS._
import play.api.libs.ws.WS
import com.redis.{RedisClientPool, RedisClient}
import play.Play

object EventBriteService extends Controller {

  val redisClients = new RedisClientPool("localhost", 6379)

  val appKey = Play.application().configuration().getString("api.eventbrite.app.key")
  val xebiaOrganizationId = "1627902102"
  val wpEventsUrl = "https://www.eventbrite.com/json/organizer_list_events"

  def events = Action {
    redisClients.withClient {
      redisClient => {

        val cacheKey = "https://www.eventbrite.com/json/organizer_list_events?id=%s".format(xebiaOrganizationId)
        val jsonEvents = redisClient.get(cacheKey).getOrElse {
          val wsRequestHolder: WSRequestHolder = WS.url(wpEventsUrl)
            .withQueryString("id" -> xebiaOrganizationId, "app_key" -> appKey)
          val jsonFetched = Json.parse(wsRequestHolder.get().value.get.body)
          val events =  (jsonFetched \\ "events").head.as[Seq[JsObject]]
            .filter { jsonEvent => List("Live").contains(jsonEvent.\("event").\("status").as[String]) }
            .map { jsonEvent => jsonEvent.\("event").as[EBEvent] }
          val jsonFormatted = Json.toJson(events).toString()
          redisClient.set(cacheKey, jsonFormatted)
          redisClient.expire(cacheKey, 60)
          jsonFormatted
        }
        Ok( jsonEvents ).as("application/json")
      }
    }
  }

}
