package models.wordpress.stripped

import play.api.libs.json._
import play.api.libs.json.JsObject
import play.api.libs.json.JsNumber

case class WPCategory(id:Long, slug:String, title:String)

object WPCategory {

  implicit object WPCategoryFormat extends Format[WPCategory] {
    def reads(json: JsValue) = JsSuccess(WPCategory(
      (json \ "id").as[Long],
      (json \ "slug").as[String],
      (json \ "title").as[String]
    ))

    def writes(category: WPCategory): JsValue = JsObject(Seq(
      "id" -> JsNumber(category.id),
      "slug" -> JsString(category.slug),
      "title" -> JsString(category.title)
    ))
  }

}
