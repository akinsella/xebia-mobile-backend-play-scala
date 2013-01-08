package cloud

import play.api.libs.concurrent.Promise
import play.api.libs.ws.WS
import play.api.libs.json.{Json, JsValue}


case class CachedWSCall(wsRequest: WS.WSRequestHolder, expiration: Option[Int] = None) {

  private lazy val cacheResponse: CachedString = CachedString(wsRequest.hashCode().toString, expiration)

  private lazy val wsCall: Promise[String] = {
    wsRequest
      .get()
      .map(_.body)
  }

  private lazy val wsData: String = getNow(wsCall)

  /**
   * @return the element from the cache or from the WS call (which would be set in cache)
   */
  def get(): String = {
    cacheResponse.getOrElse(wsData)
  }

  def getAsJson(): JsValue = {
    Json.parse(get())
  }

  def mapJson[T](extractData: (JsValue => T)): T = {
    extractData.apply(getAsJson())
  }

  private def getNow(promise: Promise[String])(implicit timeout: Long = 5000): String = {
    promise.await(timeout).fold(
      e => throw e,
      identity
    )
  }
}

object CachedWSCall {

  def apply[T](wsRequest: WS.WSRequestHolder, expiration: Int): CachedWSCall = {
    CachedWSCall(wsRequest, Some(expiration))
  }

  def apply[T](url: String, expiration: Int): CachedWSCall = {
    CachedWSCall(WS.url(url), Some(expiration))
  }

  def apply[T](url: String): CachedWSCall = {
    CachedWSCall(WS.url(url))
  }

}
