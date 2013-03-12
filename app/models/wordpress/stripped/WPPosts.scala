package models.wordpress.stripped

import play.api.libs.json._
import models.wordpress.stripped.WPCategory.WPCategoryFormat

case class WPPosts(
                    count: Int,
                    countTotal: Option[Int],
                    pages: Int,
                    posts: Seq[WPPost])

object WPPosts {

  implicit object WPPostsFormat extends Format[WPPosts] {
    def reads(json: JsValue) = JsSuccess(WPPosts(
      (json \ "count").as[Int],
      (json \ "count_total").asOpt[Int],
      (json \ "pages").as[Int],
      (json \ "posts").as[Seq[WPPost]]
    ))

    def writes(postsResponse: WPPosts): JsValue = {
      var jsonFields: Seq[(String, JsValue)] = Seq(
        "count" -> JsNumber(postsResponse.count),
        "pages" -> JsNumber(postsResponse.pages),
        "posts" -> JsArray(postsResponse.posts.map((x => WPPost.WPPostFormat.writes(x)))
        )
      )

      postsResponse.countTotal.map(c => jsonFields = jsonFields.:+("count_total" -> JsNumber(c)))

      JsObject(jsonFields)
    }

  }

}
