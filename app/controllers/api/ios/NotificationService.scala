package controllers.api.ios

import com.notnoop.apns._
import controllers.Utilities
import java.io.{FileInputStream, InputStream}
import java.security.SecureRandom
import javax.net.ssl.{TrustManager, SSLContext}
import models.notification._
import play.Play
import play.api.libs.json._
import play.api.mvc.{Action, Controller}

object NotificationService extends Controller  {

  val jssecacertsPath= "cert/jssecacerts"
  val jssecacertsPassword = Play.application().configuration().getString("notification.ios.dev.jssecacerts.password")

  val p12Path = "cert/xebia-apns.p12"
  val p12Password = Play.application().configuration().getString("notification.ios.dev.p12.password")

  System.setProperty("javax.net.debug", "all")
  System.setProperty("javax.net.ssl.trustStore", jssecacertsPath)
  System.setProperty("javax.net.ssl.trustStorePassword", jssecacertsPassword)

  def register = Action { implicit request =>
    val json: JsValue = request.body.asJson.get

    val device: Device = Device((json \ "udid").as[String], (json \ "token").as[String])
    Device.create(device)

    Created("Device registered")
  }

  def push = Action { implicit request =>

    val notification = request.body.asJson.get.as[Notification]

    println("Cert password: %s".format(jssecacertsPassword))

    val apnsService: ApnsService = APNS.newService()
      .withCert(Play.application().getFile(p12Path).getAbsolutePath, p12Password)
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