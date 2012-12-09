package utils

import java.util.Date
import org.joda.time.{DateTime, DateTimeZone}
import org.joda.time.format.{DateTimeFormatter, DateTimeFormat}
import play.api.http.HeaderNames.{LAST_MODIFIED, LOCATION}
import play.api.http.Status.CREATED
import play.api.mvc.Results.Status
import play.api.mvc.{RequestHeader, Call, PlainResult}


trait RestHelper {

  private val timeZoneCode = "GMT"

  private val df: DateTimeFormatter =
    DateTimeFormat.forPattern("EEE, dd MMM yyyy HH:mm:ss '" + timeZoneCode + "'").withLocale(java.util.Locale.ENGLISH).withZone(DateTimeZone.forID(timeZoneCode))

  def withLastModified(lastModified: Date)(p: PlainResult): PlainResult = {
    p.withHeaders((LAST_MODIFIED -> df.print(lastModified.getTime)))
  }

  def lastModifiedNow = withLastModified(new Date())(_)

  def location(locationUrl: Call, r: RequestHeader)(p: PlainResult): PlainResult = {
    p.withHeaders(LOCATION -> locationUrl.absoluteURL(false)(r))
  }

  def EntityCreated(locationUrl: Call, r: RequestHeader): PlainResult = {
    location(locationUrl, r)(Status(CREATED))
  }

  def CachedEntityCreated(locationUrl: Call, r: RequestHeader): PlainResult = {
    lastModifiedNow(EntityCreated(locationUrl, r))
  }

  def toHttpDate(date: Date): String = df.print(date.getTime)

  def fromHttpDate(s:String): DateTime = df.parseDateTime(s)

}