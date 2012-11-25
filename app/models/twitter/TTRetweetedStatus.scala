package models.twitter

import play.api.libs.json._
import play.api.libs.json.JsObject
import play.api.libs.json.JsNumber
import models.twitter.TTUser.TTUserFormat

case class TTRetweetedStatus(
                    id:Long, id_str:String, created_at:String, text:String,
                    favorited:Boolean, retweeted:Boolean, retweet_count: Long,
                    user:TTUser
                    ) {

}

object TTRetweetedStatus {

  /*
    id:tweet.id,
    id_str:tweet.id_str,
    created_at:tweet.created_at,
    text:tweet.text,
    favorited:tweet.favorited,
    retweeted:tweet.retweeted,
    retweet_count:tweet.retweet_count,
    entities:tweet.entities
  */

  implicit object TTRetweetedStatusFormat extends Format[TTRetweetedStatus] {
    def reads(json: JsValue): TTRetweetedStatus = TTRetweetedStatus(
      (json \ "id").as[Long],
      (json \ "id_str").as[String],
      (json \ "created_at").as[String],
      (json \ "text").as[String],
      (json \ "favorited").as[Boolean],
      (json \ "retweeted").as[Boolean],
      (json \ "retweet_count").as[Long],
      (json \ "user").as[TTUser]
    )

    def writes(retweetedStatus: TTRetweetedStatus): JsValue = JsObject(Seq(
      "id" -> JsNumber(retweetedStatus.id),
      "id_str" -> JsString(retweetedStatus.id_str),
      "created_at" -> JsString(retweetedStatus.created_at),
      "text" -> JsString(retweetedStatus.text),
      "favorited" -> JsBoolean(retweetedStatus.favorited),
      "retweeted" -> JsBoolean(retweetedStatus.retweeted),
      "retweet_count" -> JsNumber(retweetedStatus.retweet_count),
      "user" -> TTUserFormat.writes(retweetedStatus.user)
    ))
  }

}
