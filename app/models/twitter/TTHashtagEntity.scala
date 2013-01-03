package models.twitter

import play.api.libs.json._
import play.api.libs.json.JsNumber
import models.twitter.TTUser.TTUserFormat
import models.twitter.TTIndices.TTIndicesFormat

case class TTHashtagEntity(
                        text:String,
                        indices:TTIndices
                        ) {

}

object TTHashtagEntity {

  implicit object TTHashtagEntityFormat extends Format[TTHashtagEntity] {
    def reads(json: JsValue): TTHashtagEntity = TTHashtagEntity(
      (json \ "text").as[String],
      (json \ "indices").as[TTIndices]
    )

    def writes(hashtagEntity: TTHashtagEntity): JsValue = JsObject(Seq(
      "text" -> JsString(hashtagEntity.text),
      "indices" -> TTIndicesFormat.writes(hashtagEntity.indices)
    ))

  }

}
