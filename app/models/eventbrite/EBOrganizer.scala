package models.eventbrite

import play.api.libs.json._
import play.api.libs.json.JsObject
import play.api.libs.json.JsNumber

case class EBOrganizer(id:Long, name:String, url:String, description:String) {

}

object EBOrganizer {

  implicit object EBOrganizerFormat extends Format[EBOrganizer] {
    def reads(json: JsValue) = JsSuccess(EBOrganizer(
      (json \ "id").as[Long],
      (json \ "name").as[String],
      (json \ "url").as[String],
      (json \ "description").as[String]
    ))

    def writes(tag: EBOrganizer): JsValue = JsObject(Seq(
      "id" -> JsNumber(tag.id),
      "name" -> JsString(tag.name),
      "url" -> JsString(tag.url),
      "description" -> JsString(tag.description)
    ))
  }

}
