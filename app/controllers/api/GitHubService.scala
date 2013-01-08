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
   * @return all members of xebia-france github account
   */
  def users = Action {
    Ok {
      Json.toJson(
        CachedWSCall("https://api.github.com/orgs/xebia-france/public_members").mapJson {
          jsonFetched => jsonFetched.as[Seq[JsValue]] map (_.as[GHUser])
        }
      )
    }
  }

  /**
   * @return all repos of xebia-france github account
   */
  def repositories = Action {
    Ok {
      Json.toJson(
        CachedWSCall("https://api.github.com/orgs/xebia-france/repos").mapJson {
          jsonFetched => jsonFetched.as[Seq[JsValue]] map (_.as[GHRepository])
        }
      )
    }
  }

  /**
   * @return all owners of xebia-france github account
   */
  def owners = Action {
    Ok {
      Json.toJson(
        CachedWSCall("https://api.github.com/orgs/xebia-france/public_members").mapJson {
          jsonFetched => jsonFetched.as[Seq[JsValue]] map (_.as[GHOwner])
        }
      )
    }
  }

}
