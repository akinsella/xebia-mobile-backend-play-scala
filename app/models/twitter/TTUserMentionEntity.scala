package models.twitter

import play.api.libs.json._
import play.api.libs.json.JsNumber

case class TTUserMentionEntity(
                        id:Long,
                        id_str:String,
                        name:String,
                        screen_name:String,
                        indices:Array[Long]
                        ) {

}

object TTUserMentionEntity {

  implicit object TTUserMentionEntityFormat extends Format[TTUserMentionEntity] {
    def reads(json: JsValue): TTUserMentionEntity = TTUserMentionEntity(
      (json \ "id").as[Long],
      (json \ "id_str").as[String],
      (json \ "name").as[String],
      (json \ "screen_name").as[String],
      (json \ "indices").as[Array[Long]]
    )

    def writes(userMentionEntity: TTUserMentionEntity): JsValue = JsObject(Seq(
      "id" -> JsNumber(userMentionEntity.id),
      "id_str" -> JsString(userMentionEntity.id_str),
      "name" -> JsString(userMentionEntity.name),
      "screen_name" -> JsString(userMentionEntity.screen_name),
      "indices" -> JsArray(userMentionEntity.indices map { JsNumber(_) } )
    ))

  }

}
