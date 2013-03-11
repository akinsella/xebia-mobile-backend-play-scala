package models.wordpress

import play.api.libs.json._
import models.wordpress.WPMonth.WPMonthFormat
import play.api.libs.json.JsArray
import play.api.libs.json.JsSuccess


case class WPYear(year:Int, monthes:Option[Seq[WPMonth]])

object WPYear {

  implicit object WPYearSeqRead extends Reads[JsArray] {
    def reads(json: JsValue): JsResult[JsArray] = {

      val treeJsObject: Reads[JsObject] = (__ \ "tree").read[JsObject]
      val reads:Reads[JsArray] = treeJsObject.map({ case tree:JsObject =>
        val yearsJsonObj = tree.fields.map({ case (year, monthes:JsObject) =>
          val monthesJsonObj = monthes.fields.map({ case (month, count:JsString) =>
            Json.obj("month" -> month.toInt, "count" -> count.value.toInt)
          })
          Json.obj("year" -> year.toInt, "monthes" -> monthesJsonObj)
        })
        JsArray(yearsJsonObj)
      })

      JsSuccess(json.as[JsArray](reads))
    }
  }

  implicit object WPYearFormat extends Format[WPYear] {

    def reads(json: JsValue) = JsSuccess(WPYear(
      (json \ "year").as[Int],
      (json \ "monthes").asOpt[Seq[WPMonth]]
    ))

    def writes(year: WPYear): JsValue = {
      var jsonFields:Seq[(String, JsValue)] = Seq(
        "year" -> JsNumber(year.year)
      )

      if (year.monthes.isDefined) {
        jsonFields = jsonFields.:+("monthes" -> JsArray(year.monthes.get map { WPMonthFormat.writes(_) } ))
      }

      JsObject(jsonFields)
    }

  }

}