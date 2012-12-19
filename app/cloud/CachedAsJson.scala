package cloud

import play.api.libs.json.{Json, Format, JsValue}

/**
 * Cache an element as a json string. If not found, use the data given or
 * @param cacheKey cache key
 * @param expiration expiration for the cache
 * @param jsonFormatter write and
 * @tparam T         type of the data
 */
case class CachedAsJson[T](cacheKey: String, expiration: Option[Int] = None)(implicit jsonFormatter: Format[T]) {

  def get: Option[T] = {
    Cache
      .get(cacheKey)
      .map(x => (jsonFormatter.reads(Json.parse(x))))
  }

  def getAsJson: Option[JsValue] = {
    Cache
      .get(cacheKey)
      .map(x => (Json.parse(x)))
  }

  def getOrElse(data: => T): T = {
    this.get
      .getOrElse {
      Cache.set(cacheKey, toJsonString(data), expiration)
      data
    }
  }

  def getAsJsonOrElse(data: => T): JsValue = {
    Cache
      .get(cacheKey)
      .map(x => (Json.parse(x)))
      .getOrElse {
      Cache.set(cacheKey, toJsonString(data), expiration)
      toJson(data)
    }
  }

  private def toJsonString(data: T): String = {
    toJsonString(data).toString
  }

  private def toJson(data: T): JsValue = {
    jsonFormatter.writes(data)
  }

}