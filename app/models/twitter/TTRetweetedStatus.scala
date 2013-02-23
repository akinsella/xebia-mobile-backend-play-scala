package models.twitter

import play.api.libs.json._
import play.api.libs.json.JsObject
import play.api.libs.json.JsNumber
import models.twitter.TTUser.TTUserFormat
import models.twitter.TTEntities.TTEntitiesFormat
import java.text.SimpleDateFormat
import java.util.Locale
import java.util.Date

case class TTRetweetedStatus(
                    id:Long, id_str:String, created_at:Date, text:String,
                    favorited:Boolean, retweeted:Boolean, retweet_count: Long,
                    user:TTUser, entities:Option[TTEntities]
                    ) {

}

object TTRetweetedStatus {

  implicit object TTRetweetedStatusFormat extends Format[TTRetweetedStatus] {

    def dfTwitter = new SimpleDateFormat("EEE MMM dd HH:mm:ss Z yyyy", Locale.US)
    def dfOutput = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss")

    def reads(json: JsValue) = JsSuccess(TTRetweetedStatus(
      (json \ "id").as[Long],
      (json \ "id_str").as[String],
      dfTwitter.parse((json \ "created_at").as[String]),
      (json \ "text").as[String],
      (json \ "favorited").as[Boolean],
      (json \ "retweeted").as[Boolean],
      (json \ "retweet_count").as[Long],
      (json \ "user").as[TTUser],
      (json \ "entities").asOpt[TTEntities]
    ))

    def writes(retweetedStatus: TTRetweetedStatus): JsValue = {
      var jsonFields:Seq[(String, JsValue)] = Seq(
        "id" -> JsNumber(retweetedStatus.id),
        "id_str" -> JsString(retweetedStatus.id_str),
        "created_at" -> JsString(dfOutput.format(retweetedStatus.created_at)),
        "text" -> JsString(retweetedStatus.text),
        "favorited" -> JsBoolean(retweetedStatus.favorited),
        "retweeted" -> JsBoolean(retweetedStatus.retweeted),
        "retweet_count" -> JsNumber(retweetedStatus.retweet_count),
        "user" -> TTUserFormat.writes(retweetedStatus.user)
      )

      if (retweetedStatus.entities.isDefined) {
        jsonFields = jsonFields.:+("entities" -> TTEntitiesFormat.writes(retweetedStatus.entities.get))
      }

      JsObject(jsonFields)
    }

  }

}
