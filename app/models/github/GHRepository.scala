package models.github

import play.api.libs.json._
import play.api.libs.json.JsObject
import play.api.libs.json.JsNumber
import models.github.GHOwner.GHOwnerFormat

case class GHRepository(
                         id: Long, name: String, full_name: String, description: String, language: String,
                          owner: GHOwner,  html_url: String, homepage: String,
                         has_wiki: Boolean, has_issues: Boolean, has_downloads: Boolean, fork: Boolean,
                         watchers: Long, forks: Long, open_issues: Long, size: Long,
                         pushed_at: String, created_at: String, updated_at: String//"2012-09-10T10:23:16Z"
                         ) {

}

object GHRepository {

  implicit object GHRepositoryFormat extends Format[GHRepository] {
    def reads(json: JsValue): GHRepository = GHRepository(
      (json \ "id").as[Long],
      (json \ "name").as[String],
      (json \ "full_name").as[String],
      (json \ "description").as[String],
      (json \ "language").asOpt[String].getOrElse(""),
      (json \ "owner").as[GHOwner],
      (json \ "html_url").as[String],
      (json \ "homepage").asOpt[String].getOrElse(""),
      (json \ "has_wiki").as[Boolean],
      (json \ "has_issues").as[Boolean],
      (json \ "has_downloads").as[Boolean],
      (json \ "fork").as[Boolean],
      (json \ "watchers").as[Long],
      (json \ "forks").as[Long],
      (json \ "open_issues").as[Long],
      (json \ "size").as[Long],
      (json \ "pushed_at").as[String],
      (json \ "created_at").as[String],
      (json \ "updated_at").as[String]
    )

    def writes(repository: GHRepository): JsValue = JsObject(Seq(
      "id" -> JsNumber(repository.id),
      "name" -> JsString(repository.name),
      "full_name" -> JsString(repository.full_name),
      "description" -> JsString(repository.description),
      "language" -> JsString(repository.language),
      "owner" -> GHOwnerFormat.writes(repository.owner),
      "html_url" -> JsString(repository.html_url),
      "homepage" -> JsString(repository.homepage),
      "has_wiki" -> JsBoolean(repository.has_wiki),
      "has_issues" -> JsBoolean(repository.has_issues),
      "has_downloads" -> JsBoolean(repository.has_downloads),
      "fork" -> JsBoolean(repository.fork),
      "watchers" -> JsNumber(repository.watchers),
      "forks" -> JsNumber(repository.forks),
      "open_issues" -> JsNumber(repository.open_issues),
      "size" -> JsNumber(repository.size),
      "pushed_at" -> JsString(repository.pushed_at),
      "created_at" -> JsString(repository.created_at),
      "updated_at" -> JsString(repository.updated_at)
    ))
  }

}
