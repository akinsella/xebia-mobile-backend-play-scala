package models.twitter

import play.api.libs.json._
import play.api.libs.json.JsNumber

case class TTUrlEntity(
                       url:String,
                       expanded_url:String,
                       display_url:String,
                       indices:Array[Long]
                       ) {

}

object TTUrlEntity {

  implicit object TTUrlEntityFormat extends Format[TTUrlEntity] {
    def reads(json: JsValue): TTUrlEntity = TTUrlEntity(
      (json \ "url").as[String],
      (json \ "expanded_url").as[String],
      (json \ "display_url").as[String],
      (json \ "indices").as[Array[Long]]
    )

    def writes(urlEntity: TTUrlEntity): JsValue = JsObject(Seq(
      "url" -> JsString(urlEntity.url),
      "expanded_url" -> JsString(urlEntity.expanded_url),
      "display_url" -> JsString(urlEntity.display_url),
      "indices" -> JsArray(urlEntity.indices map { JsNumber(_) } )
    ))

  }

}
