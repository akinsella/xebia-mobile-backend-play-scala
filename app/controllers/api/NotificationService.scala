package controllers.api

import models.notification._
import play.api.mvc.{Action, Controller}
import play.api.libs.json._
import com.notnoop.apns._
import play.Play
import java.io.{FileInputStream, InputStream}

object NotificationService extends Controller  {

  val certPassword = Play.application().configuration().getString("notification.ios.dev.cert.password")
  val p12Path = "conf/Xebia2.p12"

  def register = Action { implicit request =>
    val json: JsValue = request.body.asJson.get

    val device: Device = Device((json \ "udid").as[String], (json \ "token").as[String])
    Device.create(device)

    Created("Device registered")
  }

  def push = Action { implicit request =>

    val notification = request.body.asJson.get.as[Notification]

    val apnsService: ApnsService = APNS.newService()
      .withCert(Play.application().getFile(p12Path).getAbsolutePath, certPassword)
      .withDelegate(new ApnsDelegate {
      def messageSent(notification: ApnsNotification) {
        println("Notification send: %s".format(notification))
      }

      def connectionClosed(deliveryError: DeliveryError, p2: Int) {
        println("Delivery error[%s]: %s".format(p2, deliveryError))
      }

      def messageSendFailed(notification: ApnsNotification, throwable: Throwable) {
        println("Could not send notification: '%s' - Notification: %s".format(throwable.getMessage, notification))
      }
    })
      .withReconnectPolicy(ReconnectPolicy.Provided.EVERY_HALF_HOUR)
      .withSandboxDestination()
      .build()

    val payload = APNS.newPayload().alertBody(notification.message).shrinkBody(" ...").build()

    Device.all
      .filter {
      device: Device =>
        notification.tokens.contains(device.token)
    }
      .map {
      device: Device =>
        val notification = new SimpleApnsNotification(device.token, payload)
        apnsService.push(notification)
    }

    Ok("Notified")
  }

}