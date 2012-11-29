package cloud

import com.redis.RedisClient

trait Environment {

  def withRedisClient[T](body: RedisClient => T):T

}
