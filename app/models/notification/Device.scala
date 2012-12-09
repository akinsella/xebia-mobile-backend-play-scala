package models.notification

import java.util.Date

import play.api.db._
import play.api.Play.current

import anorm._
import anorm.SqlParser._

import play.api.libs.json._
import anorm.~
import play.api.libs.json.JsString
import scala.Some
import play.api.libs.json.JsNumber

case class Device(id: Pk[Long] = NotAssigned, udid: String, token: String, createdAt: Date = new Date(), lastModified: Date = new Date()) {

  def prevNext: (Option[Device], Option[Device]) = {
    DB.withConnection {
      implicit connection =>
        val result = SQL(
          """
                (
                    select d.*, 'next' as pos from device d
                    where created_at < {date} order by created_at desc limit 1
                )
                    union
                (
                    select d.*, 'prev' as pos from device d
                    where created_at > {date} order by created_at asc limit 1
                )

                order by postedAt desc

          """).on("date" -> createdAt).as(Device.withPrevNext *).partition(_._2 == "prev")

        (result._1 match {
          case List((device, "prev")) => Some(device)
          case _ => None
        },
          result._2 match {
            case List((device, "next")) => Some(device)
            case _ => None
          })
    }
  }

}

object Device {

  def apply(udid: String, token: String) = new Device(udid = udid, token = token)


  //JSON
  implicit object DeviceFormat extends Format[Device] {
    def reads(json: JsValue): Device = Device(udid = (json \ "udid").as[String], token = (json \ "token").as[String])

    def writes(device: Device): JsValue = JsObject(Seq(
      "id" -> JsNumber(device.id.get),
      "udid" -> JsString(device.udid),
      "createdAt" -> JsNumber(device.createdAt.getTime),
      "lastModified" -> JsNumber(device.lastModified.getTime)
    ))
  }

  /**
   * Parse a Device from a ResultSet
   */
  val simple = {
    get[Pk[Long]]("device.id") ~
      get[String]("device.udid") ~
      get[String]("device.token") ~
      get[Date]("device.created_at") ~
      get[Date]("device.last_modified") map {
      case id ~ udid ~ token ~ createdAt ~ last_modified =>
        Device(id, udid, token, createdAt, last_modified)
    }
  }

  lazy val withPrevNext = {
    get[Pk[Long]]("id") ~ get[String]("udid") ~ get[String]("token") ~ get[Date]("created_at") ~ get[Date]("last_modified") ~ get[String]("pos") map {
      case id ~ udid ~ token ~ createdAt ~ lastModified ~ pos =>
        (Device(id, udid, token, createdAt,lastModified), pos)
    }
  }

  def find(field: String, value: String): Seq[Device] = {
    DB.withConnection {
      implicit connection =>
        SQL("select * from device where " + field + " = {" + field + "}")
          .on(Symbol(field) -> value).as(Device.simple *)
    }
  }

  def findById(id: Long): Option[Device] = {
    DB.withConnection {
      implicit connection =>
        SQL("select * from device where id = {id}")
          .on("id" -> id).using(simple).singleOpt()
    }
  }

  def all: List[Device] = DB.withConnection {
    implicit connection =>
      SQL(
        """
          select * from Device d
          order by d.created_at desc
        """
      ).as(Device.simple *)
  }

  def count(): Long = {
    DB.withConnection {
      implicit connection =>
        SQL("select count(*) from device").as(scalar[Long].single)
    }
  }

  /**
   * Update a device.
   *
   * @param device The device values.
   */
  def update(id: Long, device: Device): Boolean = {
    DB.withConnection {
      implicit connection =>
        SQL(
          """
          update device
          set udid = {udid}
          , token = {token}
          , last_modified = {lastModified}
          where id = {id}
          """
        ).on(
          'id -> id,
          'token -> device.token,
          'udid -> device.udid,
          'lastModified -> new Date()
        ).executeUpdate() == 1
    }
  }

  /**
   * Insert a new device.
   *
   * @param device The device values.
   */
  def create(device: Device): Option[Long] = {
    DB.withConnection {
      implicit connection => {
        SQL(
          """
          insert into device(udid, token, created_at,last_modified)
          values ({udid}, {token}, {createdAt},{lastModified})
          """
        ).on(
          'udid -> device.udid,
          'token -> device.token,
          'createdAt -> device.createdAt,
          'lastModified -> device.lastModified
        ).executeInsert()

      }
    }
  }

  /**
   * Delete a device.
   *
   * @param id Id of the device to delete.
   */
  def delete(id: Long): Boolean = {
    DB.withConnection {
      implicit connection =>
        SQL("delete from device where id = {id}").on('id -> id).executeUpdate() == 1
    }
  }

}

