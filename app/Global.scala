import models._
import play.api._
import play.api.Play.current

import anorm._

object Global extends GlobalSettings {

  override def onStart(app: Application) {
    if (!Play.isTest) {
      println("inserting seed data...")
//      InitialData.insert()
    }
  }
}

/**
 * Initial set of data to be loaded
 */
object InitialData {

  def date(str: String) = new java.text.SimpleDateFormat("yyyy-MM-dd").parse(str)

  def insert() {
//
//    if (Device.count() == 0) {
//
//      Seq(
//        Device(Id(1), "1871HKJ13Y1UIHA", "293830984298", date("2012-11-20")),
//        Device(Id(2), "209842HJ2NJ3H298", date("2012-11-21"))
//      ).foreach(Device.create)
//
//    }
//
  }
}