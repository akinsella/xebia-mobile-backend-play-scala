package controllers.api

import play.mvc.Controller
import models._
import com.codahale.jerkson.Json._

object WorkoutService extends Controller {

  def workouts = {
    response.setContentTypeIfNotSet("application/json")
    generate(Workout.find().list())
  }
  def edit(id: Long) = {
    generate(Workout.byIdWithAthleteAndComments(id))
  }

  def create() = {
    var workout = params.get("workout", classOf[Workout])
    Workout.create(workout)
  }

  def save(id: Option[Long]) = {
    var workout = params.get("workout", classOf[Workout])
    Workout.update(workout)
  }

  def delete(id: Long) = {
    Workout.delete("id={id}").on("id" -> id).executeUpdate()
  }
}