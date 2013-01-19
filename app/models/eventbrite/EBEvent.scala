package models.eventbrite

import play.api.libs.json._
import play.api.libs.json.JsObject
import play.api.libs.json.JsNumber
import models.eventbrite.EBOrganizer.EBOrganizerFormat
import models.eventbrite.EBVenue.EBVenueFormat

case class EBEvent(
                    id:Long, category:String, capacity:Long, title:String,
                    start_date:String, end_date:String, timezone_offset:String, tags:String,
                    created:String, url:String, privacy:String, status:String, description:String,
                    descriptionPlainText:String, organizer:EBOrganizer, venue:EBVenue
                    ) {

}

object EBEvent {

  implicit object EBEventFormat extends Format[EBEvent] {
    def reads(json: JsValue) = JsSuccess(EBEvent(
      (json \ "id").as[Long],
      (json \ "category").as[String],
      (json \ "capacity").as[Long],
      (json \ "title").as[String],
      (json \ "start_date").as[String],
      (json \ "end_date").as[String],
      (json \ "timezone_offset").as[String],
      (json \ "tags").as[String],
      (json \ "created").as[String],
      (json \ "url").as[String],
      (json \ "privacy").as[String],
      (json \ "status").as[String],
      (json \ "description").as[String],
      (json \ "description").as[String].replaceAll("""<(?!\/?a(?=>|\s.*>))\/?.*?>""", ""),
      (json \ "organizer").as[EBOrganizer],
      (json \ "venue").as[EBVenue]
    ))

    def writes(tag: EBEvent): JsValue = JsObject(Seq(
      "id" -> JsNumber(tag.id),
      "category" -> JsString(tag.category),
      "capacity" -> JsNumber(tag.capacity),
      "title" -> JsString(tag.title),
      "start_date" -> JsString(tag.start_date),
      "end_date" -> JsString(tag.end_date),
      "timezone_offset" -> JsString(tag.timezone_offset),
      "tags" -> JsString(tag.tags),
      "created" -> JsString(tag.created),
      "url" -> JsString(tag.url),
      "privacy" -> JsString(tag.privacy),
      "status" -> JsString(tag.status),
      "description" -> JsString(tag.description),
      "description_plain_text" -> JsString(tag.descriptionPlainText),
      "organizer" -> EBOrganizerFormat.writes(tag.organizer),
      "venue" -> EBVenueFormat.writes(tag.venue)
    ))
  }

}
