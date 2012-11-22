package models

import play.api.db._
import play.api.Play.current

import anorm._
import anorm.SqlParser._

import java.util.Date
import play.api.libs.json._
import anorm.~
import play.api.libs.json.JsArray
import play.api.libs.json.JsString
import scala.Some
import play.api.libs.json.JsNumber

case class Device(id: Pk[Long], uuid: String, createdAt: Date) {

  def prevNext: (Option[Device], Option[Device]) = {
    DB.withConnection {
      implicit connection =>
        val result = SQL(
          """
                (
                    select d.*, 'next' as pos from device d
                    where createdAt < {date} order by createdAt desc limit 1
                )
                    union
                (
                    select d.*, 'prev' as pos from device d
                    where createdAt > {date} order by createdAt asc limit 1
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

  def apply(uuid: String) = new Device(NotAssigned, uuid, null)

  implicit object DeviceFormat extends Format[Device] {
    def reads(json: JsValue): Device = Device( (json \ "uuid").as[String] )

    def writes(device: Device): JsValue = JsObject(Seq(
      "id" -> JsNumber(device.id.get),
      "uuid" -> JsString(device.uuid),
      "createdAt" -> JsNumber(device.createdAt.getTime)
    ))
  }

  /**
   * Parse a Device from a ResultSet
   */
  val simple = {
    get[Pk[Long]]("device.id") ~
      get[String]("device.uuid") ~
      get[Date]("device.createdAt") map {
      case id ~ uuid ~ createdAt =>
        Device(id, uuid, createdAt)
    }
  }

  lazy val withPrevNext = {
    get[Pk[Long]]("id") ~ get[String]("uuid") ~ get[Date]("createdAt") ~ get[String]("pos") map {
      case id ~ uuid ~ createdAt ~ pos =>
        (Device(id, uuid, createdAt), pos)
    }
  }

  def find(field: String, value: String): Seq[Device] = {
    DB.withConnection {
      implicit connection =>
        SQL("select * from device where " + field + " = {" + field + "}")
          .on(Symbol(field) -> value).as(Device.simple *)
    }
  }

  def findById(id: Option[Long]): Device = {
    DB.withConnection {
      implicit connection =>
        SQL("select * from device where id = {id}")
          .on("id" -> id.get).using(simple).single()
    }
  }

  def all: List[Device] = DB.withConnection {
    implicit connection =>
      SQL(
        """
          select * from Device d
          order by d.createdAt desc
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
  def update(device: Device) = {
    DB.withConnection {
      implicit connection =>
        SQL(
          """
          update device
          set uuid = {uuid}
          where id = {id}
          """
        ).on(
          'id -> device.id,
          'uuid -> device.uuid
        ).executeUpdate()
    }
  }

  /**
   * Insert a new device.
   *
   * @param device The device values.
   */
  def create(device: Device): Device = {
    DB.withConnection {
      implicit connection =>
        SQL(
          """
          insert into device(uuid, createdAt)
          values ({uuid}, {createdAt})
          """
        ).on(
          'uuid -> device.uuid,
          'createdAt -> device.createdAt
        ).executeInsert()
      device
    }
  }

  /**
   * Delete a device.
   *
   * @param id Id of the device to delete.
   */
  def delete(id: Long) = {
    DB.withConnection {
      implicit connection =>
        SQL("delete from device where id = {id}").on('id -> id).executeUpdate()
    }
  }

}

