package cloud

import play.api.libs.concurrent._
import play.api.libs.ws.WS
import play.api.libs.json._
import scala.concurrent._
import ExecutionContext.Implicits.global

case class CachedWSCall(wsRequest: WS.WSRequestHolder, expiration: Option[Int] = None)(implicit timeout: Long = 5000) {

  private lazy val cacheResponse: CachedString = CachedString(wsRequest.toString(), expiration)

  private lazy val wsCall: Future[Either[String, String]] = {
    wsRequest
      .get()
      .map(response => {
      response.getAHCResponse.getStatusText match {
        case "OK" => Right(response.body)
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

    responseFromCache.map(x => {
      play.Logger.debug("Response found in cache for %s".format(wsRequest.toString()))
      Right(x)
    }).getOrElse({
      play.Logger.debug("Response not found in cache for %s".format(wsRequest.toString()) + " call the external source")
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

  private def getNow(promise: Future[Either[String, String]]): Either[String, String] = {
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
