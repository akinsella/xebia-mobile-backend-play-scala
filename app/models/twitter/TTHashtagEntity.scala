package models.twitter

import play.api.libs.functional.syntax._
import play.api.libs.json._
import play.api.libs.json.JsNumber
import models.twitter.TTUser.TTUserFormat
import models.twitter.TTIndices.TTIndicesFormat

case class TTHashtagEntity(
  text:String,
  indices:TTIndices
)

object TTHashtagEntity {

  implicit object TTHashtagEntityFormat extends Format[TTHashtagEntity] {

    val hashtagEntityReads = (
      (__ \ "text").read[String] and
      (__ \ "indices").read[TTIndices]
    )(TTHashtagEntity.apply _)

    def reads(json: JsValue) = {
      json.validate[TTHashtagEntity](hashtagEntityReads)
    }

    def writes(hashtagEntity: TTHashtagEntity): JsValue = JsObject(Seq(
      "text" -> JsString(hashtagEntity.text),
      "indices" -> TTIndicesFormat.writes(hashtagEntity.indices)
    ))

  }

}
