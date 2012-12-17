package cloud

object Cache {

  def set(key: String, value: String, expiration: Option[Int] = None) = {
    Connectivity.withRedisClient {
      redisClient => {
        redisClient.set(key, value)
        if (expiration.isDefined) {
          redisClient.expire(key, expiration.get)
        }
      }
    }
  }

  def get(key: String): Option[String] = {
    Connectivity.withRedisClient {
      redisClient => {
        redisClient.get(key)
      }
    }
  }

  def getOrElse(key: String)(orElse: => String): String = {
    get(key).getOrElse(orElse)
  }

}
