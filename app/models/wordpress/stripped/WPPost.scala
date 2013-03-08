package models.wordpress.stripped

import play.api.libs.json._
import models.wordpress.stripped.WPCategory.WPCategoryFormat
import models.wordpress.stripped.WPTag.WPTagFormat
import models.wordpress.stripped.WPAuthor.WPAuthorFormat
import scala.Predef._
import play.api.libs.json.JsArray
import play.api.libs.json.JsObject
import play.api.libs.json.JsString
import play.api.libs.json.JsNumber

case class WPPost(
                   id: Long, slug: String, url: String, title: String,
                   date: String, modified: String, commentCount:Long,
                   author: WPAuthor, categories: Option[Seq[WPCategory]], tags: Option[Seq[WPTag]])

object WPPost {

  implicit object WPPostFormat extends Format[WPPost] {
    def reads(json: JsValue) = JsSuccess(WPPost(
      (json \ "id").as[Long],
      (json \ "slug").as[String],
      (json \ "url").as[String],
      (json \ "title").as[String],
      (json \ "date").as[String],
      (json \ "modified").as[String],
      (json \ "comment_count").as[Long],
      (json \ "author").as[WPAuthor],
      (json \ "categories").asOpt[Seq[WPCategory]],
      (json \ "tags").asOpt[Seq[WPTag]]
    ))

    def writes(post: WPPost): JsValue = {
      var jsonFields: Seq[(String, JsValue)] = Seq(
        "id" -> JsNumber(post.id),
        "slug" -> JsString(post.slug),
        "url" -> JsString(post.url),
        "title" -> JsString(post.title),
        "date" -> JsString(post.date),
        "modified" -> JsString(post.modified),
        "commentCount" -> JsNumber(post.commentCount),
        "author" -> WPAuthorFormat.writes(post.author)
      )

      if (post.categories.isDefined) {
        jsonFields = jsonFields.:+("categories" -> JsArray(post.categories.get map {
          WPCategoryFormat.writes(_)
        }))
      }

      if (post.tags.isDefined) {
        jsonFields = jsonFields.:+("tags" -> JsArray(post.tags.get map {
          WPTagFormat.writes(_)
        }))
      }

      JsObject(jsonFields)
    }

  }

  object WPPostIdsWrites extends Writes[WPPost] {
    def writes(post: WPPost): JsValue = JsNumber(post.id)
  }

}
