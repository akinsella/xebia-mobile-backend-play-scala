package models.twitter

import play.api.libs.json._
import play.api.libs.functional.syntax._
import models.twitter.TTIndices.TTIndicesFormat

case class TTUserMentionEntity(
  id:Long,
  id_str:String,
  name:String,
  screen_name:String,
  indices:TTIndices
)

object TTUserMentionEntity {

  implicit object TTUserMentionEntityFormat extends Format[TTUserMentionEntity] {

    val userMentionEntityReads = {
      (
        (__ \ "id").read[Long] and
        (__ \ "id_str").read[String] and
        (__ \ "name").read[String] and
        (__ \ "screen_name").read[String] and
        (__ \ "indices").read[TTIndices]
      )(TTUserMentionEntity.apply _)
    }

    def reads(json: JsValue) = {
      json.validate[TTUserMentionEntity](userMentionEntityReads)
    }

//    def reads(json: JsValue) = JsSuccess(TTUserMentionEntity(
//      (json \ "id").as[Long],
//      (json \ "id_str").as[String],
//      (json \ "name").as[String],
//      (json \ "screen_name").as[String],
//      (json \ "indices").as[TTIndices]
//    ))

    def writes(userMentionEntity: TTUserMentionEntity): JsValue = JsObject(Seq(
      "id" -> JsNumber(userMentionEntity.id),
      "id_str" -> JsString(userMentionEntity.id_str),
      "name" -> JsString(userMentionEntity.name),
      "screen_name" -> JsString(userMentionEntity.screen_name),
      "indices" -> TTIndicesFormat.writes(userMentionEntity.indices)
    ))

  }

}
