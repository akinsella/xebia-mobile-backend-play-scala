package models.twitter

import play.api.libs.json._
import play.api.libs.functional.syntax._
import play.api.libs.json.JsObject
import models.twitter.TTUserMentionEntity.TTUserMentionEntityFormat
import models.twitter.TTHashtagEntity.TTHashtagEntityFormat
import models.twitter.TTUrlEntity.TTUrlEntityFormat
import play.api.libs.functional.FunctionalBuilder

case class TTEntities(
   hashtags:Option[Seq[TTHashtagEntity]],
   urls:Option[Seq[TTUrlEntity]],
   user_mentions:Option[Seq[TTUserMentionEntity]]
)

object TTEntities {

  implicit object TTEntitiesFormat extends Format[TTEntities] {

    val entitiesReads = {
      (
        (__ \ "hashtags").readNullable[Seq[TTHashtagEntity]] and
        (__ \ "urls").readNullable[Seq[TTUrlEntity]] and
        (__ \ "user_mentions").readNullable[Seq[TTUserMentionEntity]]
      ).apply(TTEntities.apply _)
    }

    def reads(json: JsValue) = {
      json.validate[TTEntities](entitiesReads)
    }

    def writes(tweet: TTEntities): JsValue = {
      var jsonFields:Seq[(String, JsValue)] = Seq()

      if (tweet.hashtags.isDefined) {
        jsonFields = jsonFields.:+("hashtags" -> JsArray(tweet.hashtags.get map { TTHashtagEntityFormat.writes(_) } ))
      }

      if (tweet.urls.isDefined) {
        jsonFields = jsonFields.:+("urls" -> JsArray(tweet.urls.get map { TTUrlEntityFormat.writes(_) } ))
      }

      if (tweet.user_mentions.isDefined) {
        jsonFields = jsonFields.:+("user_mentions" -> JsArray(tweet.user_mentions.get map { TTUserMentionEntityFormat.writes(_) } ))
      }

      JsObject(jsonFields)
    }


  }

}
