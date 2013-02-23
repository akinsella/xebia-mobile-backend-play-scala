package securesocial.core.listener

import securesocial.core._
import play.api.mvc.RequestHeader
import play.api.mvc.Session
import play.api.Logger
import securesocial.core.LoginEvent
import securesocial.core.LogoutEvent
import play.Application

class SecurityEventListener(app: Application) extends EventListener {

  override def id: String = "my_event_listener"

  def onEvent(event: Event, request: RequestHeader, session: Session): Option[Session] = {
    val eventName = event match {
      case e: LoginEvent => "login"
      case e: LogoutEvent => "logout"
      case e: SignUpEvent => "signup"
      case e: PasswordResetEvent => "password reset"
      case e: PasswordChangeEvent => "password change"
    }
    Logger.info("traced %s event for user %s".format(eventName, event.user.fullName))
    None
  }

}