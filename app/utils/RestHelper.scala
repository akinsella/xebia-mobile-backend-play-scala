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

  /**
   * Formatter for HTTP Headers
   */
  private val df: DateTimeFormatter =
    DateTimeFormat.forPattern("EEE, dd MMM yyyy HH:mm:ss '" + timeZoneCode + "'").withLocale(java.util.Locale.ENGLISH).withZone(DateTimeZone.forID(timeZoneCode))

  /**
   * add LAST_MODIFIED header to a response
   * @param lastModified last modified date of a resource)
   * @param p the response
   * @return response with new header
   */
  def withLastModified(lastModified: Date)(p: PlainResult): PlainResult = {
    p.withHeaders((LAST_MODIFIED -> df.print(lastModified.getTime)))
  }

  /**
   * add LAST_MODIFIED header  with new Date() to a response
   * @return response with new header
   */
  def lastModifiedNow = withLastModified(new Date())(_)

  /**
   * add LOCATION header to a response
   * @param locationUrl url value of LOCATION
   * @param r request (to resolve the absolute URL of locationUrl)
   * @param p the response
   * @return response with new header
   */
  def location(locationUrl: Call, r: RequestHeader)(p: PlainResult): PlainResult = {
    p.withHeaders(LOCATION -> locationUrl.absoluteURL(false)(r))
  }

  /**
   * @param locationUrl url to access to the resource
   * @param r request (to resolve the absolute URL of locationUrl)
   * @return CREATED status with LOCATION header to the resource
   */
  def EntityCreated(locationUrl: Call, r: RequestHeader): PlainResult = {
    location(locationUrl, r)(Status(CREATED))
  }

  /**
   * @param locationUrl url to access to the resource
   * @param r request (to resolve the absolute URL of locationUrl)
   * @return CREATED status with LOCATION header to the resource and LAST_MODIFIED @ new Date()
   */
  def CachedEntityCreated(locationUrl: Call, r: RequestHeader): PlainResult = {
    lastModifiedNow(EntityCreated(locationUrl, r))
  }

  /**
   * @param date date
   * @return renders a date to HTTP Header format
   */
  def toHttpDate(date: Date): String = df.print(date.getTime)

  /**
   * @param s string date from HTTP Header format
   * @return date parsed
   */
  def fromHttpDate(s:String): DateTime = df.parseDateTime(s)

}