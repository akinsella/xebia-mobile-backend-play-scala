package controllers.api

import cloud.{CachedWSCall, Connectivity}
import models.github._
import play.api.libs.json._
import play.api.libs.ws.WS
import play.api.mvc.{Action, Controller}
import play.api.libs.ws.WS.WSRequestHolder


/**
 * Adapters for Github Webservices API for xebia-france
 */
object GitHubService extends Controller {

  /**
   * @return all members of xebia-france github account
   */
  def users = Action{
    CachedWSCall("https://api.github.com/orgs/xebia-france/public_members")(
      jsonFetched => jsonFetched.as[Seq[JsValue]] map (_.as[GHUser])
    ).okAsJson
  }

  /**
   * @return all repos of xebia-france github account
   */
  def repositories = Action{
    CachedWSCall("https://api.github.com/orgs/xebia-france/repos")(
      jsonFetched => jsonFetched.as[Seq[JsValue]] map (_.as[GHRepository])
    ).okAsJson
  }

  /**
   * @return all owners of xebia-france github account
   */
  def owners = Action{
    CachedWSCall("https://api.github.com/orgs/xebia-france/public_members")(
      jsonFetched => jsonFetched.as[Seq[JsValue]] map (_.as[GHOwner])
    ).okAsJson
  }

}
