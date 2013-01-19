package models.wordpress

import play.api.libs.json._
import models.wordpress.WPCategory.WPCategoryFormat
import models.wordpress.WPTag.WPTagFormat
import models.wordpress.WPComment.WPCommentFormat
import models.wordpress.WPAuthor.WPAuthorFormat
import scala.Predef._
import play.api.libs.json.JsArray
import play.api.libs.json.JsObject
import play.api.libs.json.JsString
import play.api.libs.json.JsNumber

case class WPPost(
                   id: Long, _type: String, slug: String, url: String, status: String, title: String, titlePlain: String,
                   content: String, excerpt: String, date: String, modified: String, commentCount: Long, commentStatus: String,
                   author: WPAuthor, categories: Option[Seq[WPCategory]], tags: Option[Seq[WPTag]], comments: Option[Seq[WPComment]]) {
}

object WPPost {

  implicit object WPPostFormat extends Format[WPPost] {
    def reads(json: JsValue) = JsSuccess(WPPost(
      (json \ "id").as[Long],
      (json \ "type").as[String],
      (json \ "slug").as[String],
      (json \ "url").as[String],
      (json \ "status").as[String],
      (json \ "title").as[String],
      (json \ "title").as[String].replaceAll( """<(?!\/?a(?=>|\s.*>))\/?.*?>""", ""),
      (json \ "content").as[String],
      (json \ "excerpt").as[String],
      (json \ "date").as[String],
      (json \ "modified").as[String],
      (json \ "comment_count").as[Long],
      (json \ "comment_status").as[String],
      (json \ "author").as[WPAuthor],
      (json \ "categories").asOpt[Seq[WPCategory]],
      (json \ "tags").asOpt[Seq[WPTag]],
      (json \ "comments").asOpt[Seq[WPComment]]
    ))

    def writes(post: WPPost): JsValue = {
      var jsonFields: Seq[(String, JsValue)] = Seq(
        "id" -> JsNumber(post.id),
        "type" -> JsString(post.slug),
        "slug" -> JsString(post.slug),
        "url" -> JsString(post.url),
        "status" -> JsString(post.status),
        "title" -> JsString(post.title),
        "titlePlain" -> JsString(post.titlePlain),
        "content" -> JsString(post.content),
        "excerpt" -> JsString(post.excerpt),
        "date" -> JsString(post.date),
        "modified" -> JsString(post.modified),
        "commentCount" -> JsNumber(post.commentCount),
        "commentStatus" -> JsString(post.commentStatus),
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

      if (post.comments.isDefined) {
        jsonFields = jsonFields.:+("comments" -> JsArray(post.comments.get map {
          WPCommentFormat.writes(_)
        }))
      }

      JsObject(jsonFields)
    }

  }

  object WPPostIdsWrites extends Writes[WPPost] {

    def writes(post: WPPost): JsValue = JsNumber(post.id)
}

}
