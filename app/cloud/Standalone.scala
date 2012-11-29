package cloud

import com.redis._
import redis._

class Standalone(host: String = "localhost", port:Int = 6379, password:Option[String] = None) extends Environment {

  lazy val redisClientPool = new RedisClientAuthenticatedPool(host, port, password)

  def withRedisClient[T](body: RedisClient => T) = {
    redisClientPool.withClient(body)
  }

}
