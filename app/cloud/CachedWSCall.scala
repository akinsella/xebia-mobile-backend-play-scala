package cloud

import play.api.libs.json.{Format, Json, JsValue}
import play.api.libs.ws.WS
import play.api.mvc.Result
import play.api.mvc.Results.Ok
import play.api.Logger


case class CachedWSCall[T](cacheKey: String, wsRequest: WS.WSRequestHolder, expiration: Option[Int])(extractData: (JsValue => T))(implicit jsonFormatter: Format[T]) {

  private val cacheElement: CachedAsJson[T] = CachedAsJson(cacheKey, expiration)

  private lazy val dataFromWS = extractData.apply(
    Json.parse(
      wsRequest
        .get()
        .await(10000)
        .fold(onError = (e => throw e)
        , onSuccess = {
          b => {
            Logger.debug(b.body)
            b.body
          }
        }
      )
    )
  )

  /**
   * @param jsonFormatter formatter needed to parse the element to JSON string from and to the cache
   * @return the element from the cache or from the WS call (which would be set in cache)
   */
  def get(implicit jsonFormatter: Format[T]): T = {
    cacheElement.getOrElse(dataFromWS)
  }

  /**
   * @param jsonFormatter formatter needed to parse the element to JSON string from and to the cache
   * @return the element as JSValue from the cache or from the WS call (which would be set in cache)
   */
  def getAsJson(implicit jsonFormatter: Format[T]): JsValue = {
    cacheElement.getAsJsonOrElse(dataFromWS)
  }

  /**
   * @param jsonFormatter formatter needed to parse the element to JSON string from and to the cache
   * @return 200 OK Response which content is the element from the cache or from the WS call (which would be set in cache)
   */
  def okAsJson(implicit jsonFormatter: Format[T]): Result = {
    Ok(getAsJson)
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
