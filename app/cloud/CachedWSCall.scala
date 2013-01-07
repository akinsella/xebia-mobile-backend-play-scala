package cloud

import play.api.libs.concurrent.Promise
import play.api.libs.ws.WS
import play.api.libs.json.{Json, JsValue}
import play.api.mvc.Results.{Status, Ok}


case class CachedWSCall(wsRequest: WS.WSRequestHolder, expiration: Option[Int] = None) {

  private lazy val cacheResponse: CachedString = CachedString(wsRequest.hashCode().toString, expiration)

  private lazy val wsCall: Promise[Either[String, String]] = {
    wsRequest
      .get()
      .map(response => {
      Status(response.getAHCResponse.getStatusCode) match {
        case Ok => Right(response.body)
        case _ => Left(response.body)
      }
    })
  }

  private lazy val wsData: Either[String, String] = getNow(wsCall)

  /**
   * @return the element from the cache or from the WS call (which would be set in cache)
   */
  def get(): Either[String, String] = {
    val responseFromCache = cacheResponse.get()

    responseFromCache.map(x => Right(x)).getOrElse({
      wsData.right.map(x => {
        cacheResponse.set(x)
      })
      wsData
    })

  }

  def getAsJson(): Either[String, JsValue] = {
    get().right.map(x => Json.parse(x))
  }

  def mapJson[T](extractData: (JsValue => T)): Either[String, T] = {
    getAsJson().right.map(x => extractData.apply(x))
  }

  private def getNow(promise: Promise[Either[String, String]])(implicit timeout: Long = 5000): Either[String, String] = {
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
