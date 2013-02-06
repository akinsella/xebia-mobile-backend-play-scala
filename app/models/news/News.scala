package models.news

import anorm._
import java.util.Date
import play.api.libs.json._
import anorm.SqlParser._
import play.api.db.DB
import play.api.Play.current

import anorm.~
import play.api.libs.json.JsObject
import play.api.libs.json.JsString
import play.api.libs.json.JsNumber
import java.text.SimpleDateFormat

case class News( id: Pk[Long], title: String, content: String, imageUrl: String, targetUrl: String,
                 draft: Boolean, publicationDate:Date,
                 createdAt:Date = new Date(), lastModified:Date = new Date() ) {

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

  def apply( id: Pk[Long], title: String, content: String, imageUrl: String,
             targetUrl: String, draft: Boolean, publicationDate: Date ) =
    new News(id, title, content, imageUrl, targetUrl, draft, publicationDate)

  def apply( title: String, content: String, imageUrl: String,
             targetUrl: String, draft: Boolean, publicationDate: Date ) =
    new News(NotAssigned, title, content, imageUrl, targetUrl, draft, publicationDate)

  //JSON
  implicit object NewsFormat extends Format[News] {
    def reads(json: JsValue): News = News(
      id = Id((json \ "id").as[Long]),
      title = (json \ "title").as[String],
      content = (json \ "content").as[String],
      imageUrl = (json \ "imageUrl").as[String],
      targetUrl = (json \ "targetUrl").as[String],
      draft = (json \ "draft").as[Boolean],
      publicationDate = new SimpleDateFormat("yyyy-MM-dd, HH:mm:ss").parse((json \ "publicationDate").as[String])
    )

    def writes(news: News): JsValue = JsObject(Seq(
      "id" -> JsNumber(news.id.get),
      "title" -> JsString(news.title),
      "content" -> JsString(news.content),
      "imageUrl" -> JsString(news.imageUrl),
      "createdAt" -> JsNumber(news.createdAt.getTime),
      "lastModified" -> JsNumber(news.lastModified.getTime),
      "draft" -> JsBoolean(news.draft),
      "publicationDate" -> JsString(new SimpleDateFormat("yyyy-MM-dd, HH:mm:ss").format(news.publicationDate))
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
      get[String]("news.targetUrl") ~
      get[Boolean]("news.draft") ~
      get[Date]("news.publicationDate") ~
      get[Date]("news.createdAt") ~
      get[Date]("news.updatedAt") map {
      case id ~ title ~ content ~ imageUrl ~ targetUrl ~ draft ~ publicationDate ~ createdAt ~ updatedAt =>
        News(id, title, content, imageUrl, targetUrl, draft, publicationDate, createdAt, updatedAt)
    }
  }

  def find(field: String, value: String): Seq[News] = {
    DB.withConnection {
      implicit connection =>
        SQL("SELECT * FROM news WHERE " + field + " = {" + field + "}")
          .on(Symbol(field) -> value).as(News.simple *)
    }
  }

  def findById(id: Long): Option[News] = {
    DB.withConnection {
      implicit connection =>
        SQL("SELECT * FROM news WHERE id = {id}")
          .on("id" -> id).using(simple).singleOpt()
    }
  }

  def all: List[News] = DB.withConnection {
    implicit connection =>
      SQL(
        """
          SELECT * FROM news n
          ORDER BY n.createdAt DESC
        """
      ).as(News.simple *)
  }

  def count(): Long = {
    DB.withConnection {
      implicit connection =>
        SQL("SELECT COUNT(*) FROM news").as(scalar[Long].single)
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
          UPDATE news
          SET title = {title}
          , content = {content}
          , imageUrl = {imageUrl}
          , targetUrl = {targetUrl}
          , draft = {draft}
          , publicationDate = {publicationDate}
          , updatedAt = {updatedAt}
          WHERE id = {id}
          """
        ).on(
          'id -> id,
          'title -> news.title,
          'content -> news.content,
          'imageUrl -> news.imageUrl,
          'targetUrl -> news.targetUrl,
          'draft -> news.draft,
          'publicationDate -> news.publicationDate,
          'updatedAt -> new Date()
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
          INSERT INTO news(title, content, imageUrl, createdAt, targetUrl, draft, publicationDate, updatedAt)
          VALUES ({title}, {content}, {imageUrl}, {createdAt}, {targetUrl}, {draft}, {publicationDate}, {updatedAt})
          """
        ).on(
          'title -> news.title,
          'content -> news.content,
          'imageUrl -> news.imageUrl,
          'createdAt -> news.createdAt,
          'targetUrl -> news.targetUrl,
          'draft -> news.draft,
          'publicationDate -> news.publicationDate,
          'updatedAt -> news.lastModified
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
        SQL("DELETE FROM news WHERE id = {id}").on('id -> id).executeUpdate() == 1
    }
  }

}

