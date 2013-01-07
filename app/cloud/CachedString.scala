package cloud


/**
 * Cache a String. If not found, use the liveData given.
 * @param cacheKey cache key
 * @param expiration expiration for the cache
 */
case class CachedString(cacheKey: String, expiration: Option[Int] = None) {

  /**
   *
   * @return the optional String in the Cache
   */
  def get(): Option[String] = Cache.get(cacheKey)

  /**
   *
   * @param liveData data if not found in the cache
   * @return the string from the cache or the live data (which will be put in the cache)
   */
  def getOrElse(liveData: => String): String = {
    this.get()
      .getOrElse {
      Cache.set(cacheKey, liveData, expiration)
      liveData
    }
  }

  /**
   * set a value in the cache for the current key and expiration parameter
   * @param value value to be put in the cache
   */
  def set(value: String) = {
    Cache.set(cacheKey, value, expiration)
  }

}