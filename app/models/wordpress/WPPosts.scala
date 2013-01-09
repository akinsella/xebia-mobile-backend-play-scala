package models.wordpress

import play.api.libs.json._
import play.api.libs.json.JsObject
import play.api.libs.json.JsNumber
import models.wordpress.WPCategory.WPCategoryFormat
import models.wordpress.WPTag.WPTagFormat
import models.wordpress.WPComment.WPCommentFormat
import models.wordpress.WPAuthor.WPAuthorFormat

case class WPPosts(
                    count: Int,
                    countTotal: Int,
                    pages: Int,
                    posts: Seq[WPPost]) {
}

object WPPosts {

  implicit object WPPostsFormat extends Format[WPPosts] {
    def reads(json: JsValue): WPPosts = WPPosts(
      (json \ "count").as[Int],
      (json \ "count_total").as[Int],
      (json \ "pages").as[Int],
      (json \ "posts").as[Seq[WPPost]]
    )

    def writes(postsResponse: WPPosts): JsValue = JsObject(Seq(
      "count" -> JsNumber(postsResponse.count),
      "count_total" -> JsNumber(postsResponse.countTotal),
      "pages" -> JsNumber(postsResponse.pages),
      "posts" -> JsArray(postsResponse.posts.map((x => WPPost.WPPostFormat.writes(x))))
    )
    )

  }

}
