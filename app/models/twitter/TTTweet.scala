package models.twitter

import play.api.libs.functional.syntax._
import play.api.libs.json._
import play.api.libs.json.JsObject
import play.api.libs.json.JsNumber
import models.twitter.TTUser.TTUserFormat
import models.twitter.TTRetweetedStatus.TTRetweetedStatusFormat
import models.twitter.TTEntities.TTEntitiesFormat
import java.text.SimpleDateFormat
import java.util.{Date, Locale}

case class
TTTweet(
  id:Long, id_str:String, created_at:Date, text:String,
  favorited:Boolean, retweeted:Boolean, retweet_count: Long,
  user:TTUser, retweetedStatus:Option[TTRetweetedStatus], entities:Option[TTEntities]
)

object TTTweet {

  implicit object TTTweetFormat extends Format[TTTweet] {

//    def dfTwitter = new SimpleDateFormat("EEE MMM dd HH:mm:ss Z yyyy", Locale.US)
    def dfOutput = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss")
//
//    def reads(json: JsValue) = JsSuccess(TTTweet(
//      (json \ "id").as[Long],
//      (json \ "id_str").as[String],
//      dfTwitter.parse((json \ "created_at").as[String]),
//      (json \ "text").as[String],
//      (json \ "favorited").as[Boolean],
//      (json \ "retweeted").as[Boolean],
//      (json \ "retweet_count").as[Long],
//      (json \ "user").as[TTUser],
//      (json \ "retweeted_status").asOpt[TTRetweetedStatus],
//      (json \ "entities").asOpt[TTEntities]
//    ))


    val createdAtReads:Reads[Date] = (__ \ "created_at").read[Date](Reads.dateReads("EEE MMM dd HH:mm:ss Z yyyy"))

    val tweetReads = {
      (
        (__ \ "id").read[Long] and
        (__ \ "id_str").read[String] and
        createdAtReads and
        (__ \ "text").read[String] and
        (__ \ "favorited").read[Boolean] and
        (__ \ "retweeted").read[Boolean] and
        (__ \ "retweet_count").read[Long] and
        (__ \ "user").read[TTUser] and
        (__ \ "retweeted_status").readNullable[TTRetweetedStatus] and
        (__ \ "entities").readNullable[TTEntities]
      )(TTTweet.apply _)
    }

    def reads(json: JsValue) = {
      json.validate[TTTweet](tweetReads)
    }

    def writes(tweet: TTTweet): JsValue = {
       var jsonFields:Seq[(String, JsValue)] = Seq(
        "id" -> JsNumber(tweet.id),
        "id_str" -> JsString(tweet.id_str),
        "created_at" -> JsString(dfOutput.format(tweet.created_at)),
        "text" -> JsString(tweet.text),
        "favorited" -> JsBoolean(tweet.favorited),
        "retweeted" -> JsBoolean(tweet.retweeted),
        "retweet_count" -> JsNumber(tweet.retweet_count),
        "user" -> TTUserFormat.writes(tweet.user)
       )

      if (tweet.retweetedStatus.isDefined) {
        jsonFields = jsonFields.:+("retweeted_status" -> TTRetweetedStatusFormat.writes(tweet.retweetedStatus.get))
      }

      if (tweet.entities.isDefined) {
        jsonFields = jsonFields.:+("entities" -> TTEntitiesFormat.writes(tweet.entities.get))
      }

       JsObject(jsonFields)
    }

  }

}
