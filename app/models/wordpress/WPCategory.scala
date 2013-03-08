package models.wordpress

import play.api.libs.json._
import play.api.libs.json.JsObject
import play.api.libs.json.JsNumber

case class WPCategory(id:Long, slug:String, title:String, parent:Long, description:String, postCount:Long)

object WPCategory {

  implicit object WPCategoryFormat extends Format[WPCategory] {
    def reads(json: JsValue) = JsSuccess(WPCategory(
      (json \ "id").as[Long],
      (json \ "slug").as[String],
      (json \ "title").as[String],
      (json \ "parent").as[Long],
      (json \ "description").as[String],
      (json \ "post_count").as[Long]
    ))

    def writes(category: WPCategory): JsValue = JsObject(Seq(
      "id" -> JsNumber(category.id),
      "slug" -> JsString(category.slug),
      "title" -> JsString(category.title),
      "parent" -> JsNumber(category.parent),
      "description" -> JsString(category.description),
      "postCount" -> JsNumber(category.postCount)
    ))
  }

}
