package cloud

import play.api.http.ContentTypes.JSON
import play.api.libs.json.{Writes, Json, JsValue}
import play.api.libs.ws.{Response, WS}
import play.api.mvc.PlainResult
import play.api.mvc.Results.Ok
import com.redis.RedisClient
import play.api.libs.concurrent.Promise


/**
 * Helper class to manage WS call and Cache
 */
object Connectivity {

  val cloudFoundry = new CloudFoundry
  val standalone = new Standalone //("localhost", 6379, Some("Some Password"))

  def env: Environment = {
    if (cloudFoundry.isActive) cloudFoundry else standalone
  }


  def withRedisClient[T](body: (RedisClient) => T) = {
    env.withRedisClient(body)
  }


  /**
   *
   * @param cacheKey key of the value in the cache
   * @param expiration optional expiration time
   * @param body the value if already present in the cache
   * @return returns the String in the cache if present with key "cacheKey"<br/>
   *         or returns "body" and put in the the cache with key "cacheKey"
   *
   */
  def withCache(cacheKey: String, expiration: Option[Int] = None)(body: => String): String = {
    env.withRedisClient(redisClient => {
      redisClient.get(cacheKey).getOrElse {
        redisClient.set(cacheKey, body)

        if (expiration.isDefined) {
          redisClient.expire(cacheKey, expiration.get)
        }

        body
      }
    })
  }

  /**
   * Look for the cache, returns it if present, or putting body in the cache and returns it
   * @param cacheKey cache key
   * @param expiration optional expiration time for cache
   * @param body value if not present in the cache
   * @return the elements from the cache of body
   */
  def withCache(cacheKey: String, expiration: Int)(body: => String): String = {
    withCache(cacheKey, Some(expiration))(body)
  }

  /**
   *
   * @param cacheKey cacheKey of the response
   * @param wsRequest WS request (with url and params) to be called if not present in the cache
   * @param expiration optional expiration time for cache
   * @param body transformation of the JsValue from the WS.get() call to get the data
   * @return 200 OK response as JSON with the content of the cache with cacheKey or the result of wsRequest (which will be put in the cache)
   */
  def getJsonWithCache[T](cacheKey: String, wsRequest: => WS.WSRequestHolder, expiration: Option[Int] = None)(body: JsValue => T)(implicit jsonFormatter: Writes[T]): PlainResult = {
    Ok(withCache(cacheKey, expiration) {

      val promise: Promise[Response] = wsRequest.get()
      val jsonFetched = Json.parse(promise.await(10000).get.body)
      Json.toJson(body.apply(jsonFetched)).toString()

    }).as(JSON)
  }

  /**
   *
   * @param url WS url to be called to fetch data if not present in the cache
   * @param expiration optional expiration time for cache
   * @param body transformation of the JsValue from the WS.get() call to get the data
   * @return 200 OK response as JSON with the content of the cache with cacheKey or the result of wsRequest (which will be put in the cache)
   */
  def getJsonWithCache[T](url: String, expiration: Option[Int])(body: JsValue => T)(implicit jsonFormatter: Writes[T]): PlainResult = {
    getJsonWithCache(url, WS.url(url), expiration)(body)(jsonFormatter)
  }

  /**
   *
   * @param url WS url to be called to fetch data if not present in the cache
   * @param body transformation of the JsValue from the WS.get() call to get the data
   * @return 200 OK response as JSON with the content of the cache with cacheKey or the result of wsRequest (which will be put in the cache)
   */
  def getJsonWithCache[T](url: String)(body: JsValue => T)(implicit jsonFormatter: Writes[T]): PlainResult = {
    getJsonWithCache(url, WS.url(url), None)(body)(jsonFormatter)
  }

}
