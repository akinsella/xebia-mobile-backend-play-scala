package models.twitter

import play.api.libs.json._
import play.api.libs.functional.syntax._
import play.api.libs.json.JsObject
import play.api.libs.json.JsNumber
import java.text.SimpleDateFormat
import java.util.Locale
import java.util.Date

case class TTUser(
  id:Long, id_str: String, name: String, screen_name: String, profile_image_url: String,
  location: String, url: String, description: String, _protected: Boolean, createdAt: Date,
  followersCount:Long, friendsCount: Long, listedCount: Long, statusesCount: Long,
  favouritesCount: Long, utcOffset: Long, timeZone: String,
  lang: String
)

object TTUser {

  implicit object TTUserFormat extends Format[TTUser] {

    val createdAtReads:Reads[Date] = (__ \ "created_at").read[Date](Reads.dateReads("EEE MMM dd HH:mm:ss Z yyyy"))

    val userReads:Reads[TTUser] = {
      (
        (__ \ "id").read[Long] and
        (__ \ "id_str").read[String] and
        (__ \ "name").read[String] and
        (__ \ "screen_name").read[String] and
        (__ \ "profile_image_url").read[String] and
        (__ \ "location").read[String] and
        (__ \ "url").read[String] and
        (__ \ "description").read[String] and
        (__ \ "protected").read[Boolean] and
        createdAtReads and
        (__ \ "followers_count").read[Long] and
        (__ \ "friends_count").read[Long] and
        (__ \ "listed_count").read[Long] and
        (__ \ "statuses_count").read[Long] and
        (__ \ "favourites_count").read[Long] and
        (__ \ "utc_offset").read[Long] and
        (__ \ "time_zone").read[String] and
        (__ \ "lang").read[String]
      )(TTUser.apply _)
    }

    def reads(json: JsValue) = {
      json.validate[TTUser](userReads)
    }


//    def dfTwitter = new SimpleDateFormat("EEE MMM dd HH:mm:ss Z yyyy", Locale.US)
    def dfOutput = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss")
//    def reads(json: JsValue) = JsSuccess(TTUser(
//      (json \ "id").as[Long],
//      (json \ "id_str").as[String],
//      (json \ "name").as[String],
//      (json \ "screen_name").as[String],
//      (json \ "profile_image_url").as[String],
//      (json \ "location").as[String],
//      (json \ "url").as[String],
//      (json \ "description").as[String],
//      (json \ "protected").as[Boolean],
//      dfTwitter.parse((json \ "created_at").as[String]),
//      (json \ "followers_count").as[Long],
//      (json \ "friends_count").as[Long],
//      (json \ "listed_count").as[Long],
//      (json \ "favourites_count").as[Long],
//      (json \ "statuses_count").as[Long],
//      (json \ "utc_offset").as[Long],
//      (json \ "time_zone").as[String],
//      (json \ "lang").as[String]
//    ))



    def writes(user: TTUser): JsValue = JsObject(Seq(
      "id" -> JsNumber(user.id),
      "id_str" -> JsString(user.id_str),
      "name" -> JsString(user.name),
      "screen_name" -> JsString(user.screen_name),
      "profile_image_url" -> JsString(user.profile_image_url),
      "location" -> JsString(user.location),
      "url" -> JsString(user.url),
      "description" -> JsString(user.description),
      "protected" -> JsBoolean(user._protected),
      "created_at" -> JsString(dfOutput.format(user.createdAt)),
      "followers_count" -> JsNumber(user.followersCount),
      "friends_count" -> JsNumber(user.friendsCount),
      "listed_count" -> JsNumber(user.listedCount),
      "favourites_count" -> JsNumber(user.favouritesCount),
      "statuses_count" -> JsNumber(user.statusesCount),
      "utc_offset" -> JsNumber(user.utcOffset),
      "time_zone" -> JsString(user.timeZone),
      "lang" -> JsString(user.lang)
    ))
  }

}
