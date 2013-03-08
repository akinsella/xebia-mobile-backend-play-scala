package models.wordpress.stripped

import play.api.libs.json._
import play.api.libs.json.JsObject
import play.api.libs.json.JsNumber

case class WPTag(id:Long, slug:String, title:String)

object WPTag {

  implicit object WPTagFormat extends Format[WPTag] {
    def reads(json: JsValue) = JsSuccess(WPTag(
      (json \ "id").as[Long],
      (json \ "slug").as[String],
      (json \ "title").as[String]
    ))

    def writes(tag: WPTag): JsValue = JsObject(Seq(
      "id" -> JsNumber(tag.id),
      "slug" -> JsString(tag.slug),
      "title" -> JsString(tag.title)
    ))
  }

}
