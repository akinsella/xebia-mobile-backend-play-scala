package models.eventbrite

import play.api.libs.json._
import play.api.libs.json.JsObject
import play.api.libs.json.JsNumber

case class EBVenue(
                    id:Long, name:String, city:String, region:String, country:String, country_code:String,
                    address:String, address_2:String, postal_code:String, longitude:Double, latitude:Double
                    ) {

}

object EBVenue {

  implicit object EBVenueFormat extends Format[EBVenue] {
    def reads(json: JsValue) = JsSuccess(EBVenue(
      (json \ "id").as[Long],
      (json \ "name").as[String],
      (json \ "city").as[String],
      (json \ "region").as[String],
      (json \ "country").as[String],
      (json \ "country_code").as[String],
      (json \ "address").as[String],
      (json \ "address_2").as[String],
      (json \ "postal_code").as[String],
      (json \ "longitude").as[Double],
      (json \ "latitude").as[Double]
    ))

    def writes(tag: EBVenue): JsValue = JsObject(Seq(
      "id" -> JsNumber(tag.id),
      "name" -> JsString(tag.name),
      "city" -> JsString(tag.city),
      "region" -> JsString(tag.region),
      "country" -> JsString(tag.country),
      "country_code" -> JsString(tag.country_code),
      "address" -> JsString(tag.address),
      "address_2" -> JsString(tag.address_2),
      "postal_code" -> JsString(tag.postal_code),
      "longitude" -> JsNumber(tag.longitude),
      "latitude" -> JsNumber(tag.latitude)
    ))
  }

}
