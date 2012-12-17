package redis

import com.redis.{RedisConnectionException, RedisClient}

import org.apache.commons.pool._
import org.apache.commons.pool.impl._

private [redis] class RedisClientFactory(host: String, port: Int, password:Option[String], database: Int = 0) extends PoolableObjectFactory[RedisClient] {

  // when we make an object it's already connected
  def makeObject = {
    val cl = new RedisClient(host, port)
    if (database != 0) {
      cl.select(database)
    }

    if (password.isDefined) {
      if (!cl.auth(password.get)) {
        throw new RedisConnectionException("Wrong password")
      }
      else {
        play.Logger.info("Authenticated against Redis")
      }
    }

    cl
  }

  // quit & disconnect
  def destroyObject(rc: RedisClient): Unit = {
    rc.quit // need to quit for closing the connection
    rc.disconnect // need to disconnect for releasing sockets
  }

  // noop: we want to have it connected
  def passivateObject(rc: RedisClient): Unit = {}
  def validateObject(rc: RedisClient) = rc.connected == true

  // noop: it should be connected already
  def activateObject(rc: RedisClient): Unit = {}
}

class RedisClientAuthenticatedPool(host: String, port: Int, password:Option[String], maxIdle: Int = 8, database: Int = 0) {
  val pool = new StackObjectPool(new RedisClientFactory(host, port, password, database), maxIdle)
  override def toString = host + ":" + String.valueOf(port)

  def withClient[T](body: RedisClient => T) = {
    val client = pool.borrowObject
    try {
      body(client)
    } finally {
      pool.returnObject(client)
    }
  }

  // close pool & free resources
  def close = pool.close
}

