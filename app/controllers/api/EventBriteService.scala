package controllers.api

import cloud.Connectivity
import models.eventbrite.EBEvent
import play.Play
import play.api.libs.json._
import play.api.libs.ws.WS
import play.api.mvc.{Action, Controller}

/**
 * Adapters for EventBrite Webservices API
 */
object EventBriteService extends Controller {

  private val appKey = Play.application().configuration().getString("api.eventbrite.app.key")
  private val xebiaOrganizationId = "1627902102"
  private val wpEventsUrl = "https://www.eventbrite.com/json/organizer_list_events"

  /**
   * @return get the "Live" events from Xebia Organization
   */
  def events = Action {
    val cacheKey = wpEventsUrl + "?id=%s".format(xebiaOrganizationId)

    val wsRequestHolder = WS
      .url(wpEventsUrl)
            .withQueryString("id" -> xebiaOrganizationId, "app_key" -> appKey)

    Connectivity.getJsonWithCache(cacheKey, wsRequestHolder) {
      jsonFetched => (jsonFetched \\ "events").head.as[Seq[JsObject]]
        .filter(jsonEvent => List("Live").contains(jsonEvent.\("event").\("status").as[String]))
        .map(_.\("event").as[EBEvent])
    }
  }

}
