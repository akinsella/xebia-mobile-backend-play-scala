package cloud

import play.api.libs.json.{Format, Json, JsValue}
import play.api.libs.ws.WS
import play.api.mvc.{AsyncResult, Result}
import play.api.mvc.Results.Ok
import play.api.libs.concurrent.{NotWaiting, Promise}


case class CachedWSCall[T](cacheKey: String, wsRequest: WS.WSRequestHolder, expiration: Option[Int])(extractData: (JsValue => T))(implicit jsonFormatter: Format[T]) {

  private val cacheElement: CachedAsJson[T] = CachedAsJson(cacheKey, expiration)

  private lazy val dataFromWS: Promise[T] = {
    wsRequest
      .get()
      .map(response => {
      extractData.apply(Json.parse(response.body))
    })
  }

  /**
   * @return the element from the cache or from the WS call (which would be set in cache)
   */
  def get: Promise[T] = {
    dataFromWS.map(d => cacheElement.getOrElse(d))
  }

  /**
   * @return the element as JSValue from the cache or from the WS call (which would be set in cache)
   */
  def getAsJson: Promise[JsValue] = {
    dataFromWS.map(d => cacheElement.getAsJsonOrElse(d))
  }

  /**
   * @return 200 OK Response which content is the element from the cache or from the WS call (which would be set in cache)
   */
  def okAsJson: AsyncResult = {
    AsyncResult {
      getAsJson.map(response => Ok(response))
    }
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

  def Now[A](promise: Promise[A])(implicit timeout: Long = 5000): A = {
    promise.await(timeout).fold(
      e => throw e,
      identity
    )
  }
}
