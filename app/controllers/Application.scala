package controllers

import play.api.mvc._
import play.api.data._
import play.api.data.Forms._
import models.security.User

import views._
import models.notification.Device

object Application extends Controller {

  def index = Action { request =>
    val user = User("jdoe@example.org", "John Does", "Password123")
    Ok(views.html.Application.index("Your new application is ready.", user))
  }

  // -- Authentication

  val loginForm = Form(
    tuple(
      "email" -> text,
      "password" -> text
    ) verifying ("Invalid email or password", result => result match {
      case (email, password) => User.authenticate(email, password).isDefined
    })
  )

  /**
   * Login page.
   */
  def login = Action { implicit request =>
    Ok(views.html.Application.login(loginForm))
  }

  /**
   * Handle login form submission.
   */
  def authenticate = Action { implicit request =>
    loginForm.bindFromRequest.fold(
      formWithErrors => BadRequest(views.html.Application.login(formWithErrors)),
      user => Redirect(routes.Application.index()).withSession("email" -> user._1)
    )
  }

  /**
   * Logout and clean the session.
   */
  def logout = Action {
    Redirect(routes.Application.login()).withNewSession.flashing(
      "success" -> "You've been logged out"
    )
  }
  /**
   * Provide security features
   */
  trait Secured {

    /**
     * Retrieve the connected user email.
     */
    private def username(request: RequestHeader) = request.session.get("email")

    /**
     * Redirect to login if the user in not authorized.
     */
    private def onUnauthorized(request: RequestHeader) = Results.Redirect(routes.Application.login())

    // --

    /**
     * Action for authenticated users.
     */
    def IsAuthenticated(f: => String => Request[AnyContent] => Result) = Security.Authenticated(username, onUnauthorized) { user =>
      Action(request => f(user)(request))
    }

  }

}
