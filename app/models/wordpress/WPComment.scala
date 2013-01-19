package models.wordpress

import play.api.libs.json._
import play.api.libs.json.JsObject
import play.api.libs.json.JsNumber

case class WPComment(
                   id:Long, name:String, url:String, date:String, content:String, parent:Long) {
}

object WPComment {

  implicit object WPCommentFormat extends Format[WPComment] {
    def reads(json: JsValue) = JsSuccess(WPComment(
      (json \ "id").as[Long],
      (json \ "name").as[String],
      (json \ "url").as[String],
      (json \ "date").as[String],
      (json \ "content").as[String],
      (json \ "parent").as[Long]
    ))

    def writes(post: WPComment): JsValue = JsObject(Seq(
      "id" -> JsNumber(post.id),
      "name" -> JsString(post.name),
      "url" -> JsString(post.url),
      "date" -> JsString(post.date),
      "content" -> JsString(post.content),
      "parent" -> JsNumber(post.parent)
    ))
  }

}
