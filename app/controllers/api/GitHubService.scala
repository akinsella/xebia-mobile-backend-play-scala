package controllers.api

import cloud.CachedWSCall
import models.github._
import play.api.libs.json._
import play.api.mvc.{Action, Controller}


/**
 * Adapters for Github Webservices API for xebia-france
 */
object GitHubService extends Controller {

  /**
   * @return all repos of xebia-france github account
   */
  def repositories = Action {
    CachedWSCall("https://api.github.com/orgs/xebia-france/repos")
      .mapJson {
        jsonFetched => jsonFetched.as[Seq[JsValue]] map (_.as[GHRepository])
      }
      .fold(
        errorMessage => {
          InternalServerError(errorMessage)
        },
        response => {
          Ok(Json.toJson(response))
        }
      )
  }

  /**
   * @return all owners of xebia-france github account
   */
  def owners = Action {
    CachedWSCall("https://api.github.com/orgs/xebia-france/public_members")
      .mapJson {
        jsonFetched => jsonFetched.as[Seq[JsValue]] map (_.as[GHOwner])
      }
      .fold(
        errorMessage => {
          InternalServerError(errorMessage)
        },
        response => {
          Ok(Json.toJson(response))
        }
      )
  }

}
