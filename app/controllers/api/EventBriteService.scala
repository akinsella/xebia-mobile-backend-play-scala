package controllers.api

import models._
import eventbrite.EBEvent
import play.api.mvc.{Action, Controller}
import play.api.libs.json._
import play.libs.WS
import com.redis.{RedisClientPool, RedisClient}
import play.libs.WS.WSRequestHolder
import play.Play

object EventBriteService extends Controller {

  val redisClients = new RedisClientPool("localhost", 6379)

  val appKey = Play.application().configuration().getString("api.eventbrite.app.key")
  val xebiaOrganizationId = "1627902102"
  val wpEventsUrl = "https://www.eventbrite.com/json/organizer_list_events"

  def events = Action {
    redisClients.withClient {
      redisClient => {
        println("WS Events Url: " + wpEventsUrl)

        val jsonEvents = redisClient.get(wpEventsUrl).getOrElse {
          val wsRequestHolder: WSRequestHolder = WS.url(wpEventsUrl)
            .setQueryParameter("id", xebiaOrganizationId)
            .setQueryParameter("app_key", appKey)

          val json = Json.parse(wsRequestHolder.get.get.getBody)
          println(json)

          val events =  (json \\ "events").head.as[Seq[JsObject]]
            .filter { jsonEvent => List("Live").contains(jsonEvent.\("event").\("status").as[String]) }
            .map { jsonEvent => jsonEvent.\("event").as[EBEvent] }
          val jsonEvents = Json.toJson(events).toString()
          redisClient.set(wpEventsUrl, jsonEvents)
          redisClient.expire(wpEventsUrl, 60)

          jsonEvents
        }
        Ok( jsonEvents ).as("application/json")
      }
    }
  }

}
