package cloud

object Cache {

  def get(key: String): Option[String] = {
    Connectivity.withRedisClient {
      redisClient => {
        redisClient.get(key)
      }
    }
  }

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

  def getOrElse(key: String, expiration: Option[Int] = None)(liveValue: => String): String = {
    get(key).getOrElse {
      set(key, liveValue, expiration)
      liveValue
    }
  }

}
