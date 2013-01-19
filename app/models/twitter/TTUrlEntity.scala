package models.twitter

import play.api.libs.json._
import models.twitter.TTIndices.TTIndicesFormat

case class TTUrlEntity(
                       url:String,
                       expanded_url:String,
                       display_url:String,
                       indices:TTIndices
                       ) {

}

object TTUrlEntity {

  implicit object TTUrlEntityFormat extends Format[TTUrlEntity] {
    def reads(json: JsValue) = JsSuccess(TTUrlEntity(
      (json \ "url").as[String],
      (json \ "expanded_url").as[String],
      (json \ "display_url").as[String],
      (json \ "indices").as[TTIndices]
    ))

    def writes(urlEntity: TTUrlEntity): JsValue = JsObject(Seq(
      "url" -> JsString(urlEntity.url),
      "expanded_url" -> JsString(urlEntity.expanded_url),
      "display_url" -> JsString(urlEntity.display_url),
      "indices" -> TTIndicesFormat.writes(urlEntity.indices)
    ))

  }

}
