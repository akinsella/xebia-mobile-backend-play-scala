package cloud

import org.cloudfoundry.runtime.env._
import com.redis.RedisClient
import redis.RedisClientAuthenticatedPool

class CloudFoundry extends Environment {

  lazy val cloudEnvironment = new CloudEnvironment()

  lazy val isActive: Boolean = cloudEnvironment.isCloudFoundry

  lazy val redis = new CloudFoundryRedis(cloudEnvironment)

  def withRedisClient[T](body: RedisClient => T) = redis.withClient(body)

}

class CloudFoundryRedis(cloudEnvironment:CloudEnvironment) {

  val redisServices = cloudEnvironment.getServiceInfos(classOf[RedisServiceInfo])
  val redisService = redisServices.get(0)
  val redisClientPool = new RedisClientAuthenticatedPool(redisService.getHost, redisService.getPort, Option(redisService.getPassword))

  def withClient[T](body: RedisClient => T) = {
    redisClientPool.withClient(body)
  }

}
