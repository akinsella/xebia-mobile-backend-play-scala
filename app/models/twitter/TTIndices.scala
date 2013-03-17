package models.twitter

import play.api.libs.json._
import play.api.libs.json.JsObject


case class TTIndices(
  startOffset:Long,
  endOffset:Long
)

object TTIndices {

  implicit object TTIndicesFormat extends Format[TTIndices] {

    def reads(json: JsValue) = JsSuccess(TTIndices(
      (json.as[Seq[JsValue]].head).as[Long],
      (json.as[Seq[JsValue]].last).as[Long]
    ))

    def writes(indices: TTIndices): JsValue = JsObject(Seq(
      "start" -> JsNumber(indices.startOffset),
      "end" -> JsNumber(indices.endOffset)
    ))

  }

}
