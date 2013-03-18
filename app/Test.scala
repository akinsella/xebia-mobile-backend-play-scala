import java.util.Date
import play.api.libs.json._
import play.api.libs.functional.syntax._
import play.api.libs.json.JsObject
import play.api.libs.json.JsNumber
import play.api.libs.json.{Json, Reads}

class Test extends App {
  val dateReads: Reads[Date] = Reads.dateReads("EEE MMM dd HH:mm:ss Z yyyy")

  val json = Json.obj("date" -> "Wed May 27 06:44:36 +0000 2009")


  val reads:Reads[Date] = (__ \ "date").read[Date](dateReads)
  println( json.validate[Date])
}
