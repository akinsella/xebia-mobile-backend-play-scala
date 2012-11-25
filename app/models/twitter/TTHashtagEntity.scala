package models.twitter

import play.api.libs.json._
import play.api.libs.json.JsNumber

case class TTHashtagEntity(
                        text:String,
                        indices:Array[Long]
                        ) {

}

object TTHashtagEntity {

  implicit object TTHashtagEntityFormat extends Format[TTHashtagEntity] {
    def reads(json: JsValue): TTHashtagEntity = TTHashtagEntity(
      (json \ "text").as[String],
      (json \ "indices").as[Array[Long]]
    )

    def writes(hashtagEntity: TTHashtagEntity): JsValue = JsObject(Seq(
      "text" -> JsString(hashtagEntity.text),
      "indices" -> JsArray(hashtagEntity.indices map { JsNumber(_) } )
    ))

  }

}
