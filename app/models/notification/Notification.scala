package models.notification

import play.api.libs.json._

case class Notification( message:String, tokens:Array[String] ) {

}

object Notification {

  implicit object NotificationFormat extends Format[Notification] {
    def reads(json: JsValue) = JsSuccess(Notification(
      (json \ "message").as[String],
      (json \ "tokens").as[Array[String]]
    ))

    def writes(notification: Notification): JsValue = JsObject(Seq(
      "message" -> JsString(notification.message),
      "tokens" -> JsArray(notification.tokens map { JsString(_) } )
    ))

  }

}
