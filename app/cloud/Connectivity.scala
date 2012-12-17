package cloud

import com.redis.RedisClient

/**
 * Helper class to manage WS call and Cache
 */
object Connectivity {

  val cloudFoundry = new CloudFoundry
  val standalone = new Standalone //("localhost", 6379, Some("Some Password"))

  def env: Environment = {
    if (cloudFoundry.isActive) {
      play.Logger.info("Running on cloud[CloudFoundry]")
      cloudFoundry
    } else {
      play.Logger.info("Running as Standalone application")
      standalone
    }
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
}
