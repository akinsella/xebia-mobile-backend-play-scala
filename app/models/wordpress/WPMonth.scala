package models.wordpress

import play.api.libs.json._
import play.api.libs.json.JsSuccess
import play.api.libs.json.JsNumber
import play.api.libs.json.JsObject


case class WPMonth(month:Int, count:Int)

object WPMonth {

  implicit object WPMonthFormat extends Format[WPMonth] {

    def reads(json: JsValue) = {
      JsSuccess(WPMonth(
        (json \ "month").as[Int],
        (json \ "count").as[Int]
      ))
    }

    def writes(month: WPMonth): JsValue = JsObject(Seq(
      "month" -> JsNumber(month.month),
      "count" -> JsNumber(month.count)
    ))

  }

}