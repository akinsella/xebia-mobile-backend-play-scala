package models.news

import anorm._
import java.util.Date
import play.api.libs.json._
import anorm.SqlParser._
import play.api.db.DB
import anorm.~
import play.api.libs.json.JsObject
import play.api.libs.json.JsString
import play.api.libs.json.JsNumber
import play.api.Play.current

import play.api.data._
import play.api.data.Forms._
import play.api.data.validation.Constraints._

case class News( id: Pk[Long] = NotAssigned, title:String, content:String, imageUrl:String,
            createdAt: Date = new Date(), lastModified: Date = new Date() ) {

/*
  val newsForm = Form(
    mapping(
      "title" -> text.verifying(nonEmpty),
      "content" -> text.verifying(nonEmpty),
      "imageUrl" -> text.verifying(nonEmpty)
    )(News.apply)(News.unapply)
  )
*/

}

object News {

  def apply(title: String, content: String, imageUrl: String) = new News(title = title, content = content, imageUrl = imageUrl)

  def unapply(news: News):(String, String, String) = (news.title, news.content, news.imageUrl)

  //JSON
  implicit object NewsFormat extends Format[News] {
    def reads(json: JsValue): News = News(
      title = (json \ "title").as[String],
      content = (json \ "content").as[String],
      imageUrl = (json \ "imageUrl").as[String]
    )


    def writes(news: News): JsValue = JsObject(Seq(
      "id" -> JsNumber(news.id.get),
      "title" -> JsString(news.title),
      "content" -> JsString(news.content),
      "imageUrl" -> JsString(news.imageUrl),
      "createdAt" -> JsNumber(news.createdAt.getTime),
      "lastModified" -> JsNumber(news.lastModified.getTime)
    ))
  }

  /**
   * Parse a news from a ResultSet
   */
  val simple = {
    get[Pk[Long]]("news.id") ~
      get[String]("news.title") ~
      get[String]("news.content") ~
      get[String]("news.imageUrl") ~
      get[Date]("news.created_at") ~
      get[Date]("news.last_modified") map {
      case id ~ title ~ content ~imageUrl ~ createdAt ~ last_modified =>
        News(id, title, content, imageUrl, createdAt, last_modified)
    }
  }

  def find(field: String, value: String): Seq[News] = {
    DB.withConnection {
      implicit connection =>
        SQL("select * from news where " + field + " = {" + field + "}")
          .on(Symbol(field) -> value).as(News.simple *)
    }
  }

  def findById(id: Long): Option[News] = {
    DB.withConnection {
      implicit connection =>
        SQL("select * from news where id = {id}")
          .on("id" -> id).using(simple).singleOpt()
    }
  }

  def all: List[News] = DB.withConnection {
    implicit connection =>
      SQL(
        """
          select * from news n
          order by n.created_at desc
        """
      ).as(News.simple *)
  }

  def count(): Long = {
    DB.withConnection {
      implicit connection =>
        SQL("select count(*) from news").as(scalar[Long].single)
    }
  }

  /**
   * Update a news.
   *
   * @param news The news values.
   */
  def update(id: Long, news: News): Boolean = {
    DB.withConnection {
      implicit connection =>
        SQL(
          """
          update news
          set title = {title}
          , content = {content}
          , imageUrl = {imageUrl}
          , last_modified = {lastModified}
          where id = {id}
          """
        ).on(
          'id -> id,
          'title -> news.title,
          'content -> news.content,
          'imageUrl -> news.imageUrl,
          'lastModified -> new Date()
        ).executeUpdate() == 1
    }
  }

  /**
   * Insert a news.
   *
   * @param news The news values.
   */
  def create(news: News): Option[Long] = {
    DB.withConnection {
      implicit connection => {
        SQL(
          """
          insert into news(title, content, imageUrl, created_at, last_modified)
          values ({title}, {content}, {imageUrl}, {createdAt}, {lastModified})
          """
        ).on(
          'title -> news.title,
          'content -> news.content,
          'imageUrl -> news.imageUrl,
          'createdAt -> news.createdAt,
          'lastModified -> news.lastModified
        ).executeInsert()

      }
    }
  }

  /**
   * Delete a news.
   *
   * @param id Id of the news to delete.
   */
  def delete(id: Long): Boolean = {
    DB.withConnection {
      implicit connection =>
        SQL("delete from news where id = {id}").on('id -> id).executeUpdate() == 1
    }
  }

}

