package cloud

import play.api.libs.json.{Format, Json, JsValue}
import play.api.libs.ws.WS
import play.api.mvc.{Action, AsyncResult, Result}
import play.api.mvc.Results.Ok
import play.api.libs.concurrent.{NotWaiting, Promise}
import play.Play


case class CachedWSCall[T](cacheKey: String, wsRequest: WS.WSRequestHolder, expiration: Option[Int])(extractData: (JsValue => T))(implicit jsonFormatter: Format[T]) {

  private lazy val cacheElement: CachedAsJson[T] = CachedAsJson(cacheKey, expiration)

  private lazy val wsCall: Promise[T] = {
    wsRequest
      .get()
      .map(response => {
      extractData.apply(Json.parse(response.body))
    })
  }

  private lazy val wsData: T = getNow(wsCall)

  /**
   * @return the element from the cache or from the WS call (which would be set in cache)
   */
  def get: T = {
    cacheElement.getOrElse(wsData)
  }

  /**
   * @return the element as JSValue from the cache or from the WS call (which would be set in cache)
   */
  def getAsJson: JsValue = {
    cacheElement.getAsJsonOrElse(wsData)
  }

  /**
   * @return 200 OK Response which content is the element from the cache or from the WS call (which would be set in cache)
   */
  def okAsJson: Result = {
    Ok {
      getAsJson
    }
  }

  private def getNow(promise: Promise[T])(implicit timeout: Long = 5000): T = {
    promise.await(timeout).fold(
      e => throw e,
      identity
    )
  }

}

object CachedWSCall {

  def apply[T](url: String, expiration: Int)(extractData: (JsValue => T))(implicit jsonFormatter: Format[T]): CachedWSCall[T] = {
    CachedWSCall(url, WS.url(url), Some(expiration))(extractData)
  }

  def apply[T](url: String)(extractData: (JsValue => T))(implicit jsonFormatter: Format[T]): CachedWSCall[T] = {
    CachedWSCall(url, WS.url(url), None)(extractData)
  }

  def apply[T](cacheKey: String, wsRequest: WS.WSRequestHolder)(extractData: (JsValue => T))(implicit jsonFormatter: Format[T]): CachedWSCall[T] = {
    CachedWSCall(cacheKey, wsRequest, None)(extractData)
  }

}
