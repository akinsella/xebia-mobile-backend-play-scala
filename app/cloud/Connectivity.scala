package cloud

import com.redis.RedisClient


object Connectivity extends Environment {

  val cloudFoundry = new CloudFoundry
  val standalone = new Standalone //("localhost", 6379, Some("Some Password"))

  def env: Environment = {
    if (cloudFoundry.isActive) cloudFoundry else standalone
  }

  def withRedisClient[T](body: (RedisClient) => T) = {
    env.withRedisClient(body)
  }
}
