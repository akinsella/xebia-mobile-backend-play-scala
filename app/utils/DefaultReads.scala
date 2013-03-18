package utils

import play.api.libs.json._
import play.api.data.validation._
import _root_.java.util.Locale

object DefaultReads {

  /**
   * Reads for the `java.util.Date` type.
   *
   * @param pattern a date pattern, as specified in `java.text.SimpleDateFormat`.
   * @param locale a locale, as specified in `java.text.SimpleDateFormat`.
   * @param corrector a simple string transformation function that can be used to transform input String before parsing. Useful when standards are not exactly respected and require a few tweaks
   */
  def dateReads(pattern: String, locale:Locale = Locale.getDefault, corrector: String => String = identity): Reads[java.util.Date] = new Reads[java.util.Date] {
    val df = new java.text.SimpleDateFormat(pattern, locale)

    def reads(json: JsValue): JsResult[java.util.Date] = json match {
      case JsNumber(d) => JsSuccess(new java.util.Date(d.toLong))
      case JsString(s) => parseDate(corrector(s)) match {
        case Some(d) => JsSuccess(d)
        case None => JsError(Seq(JsPath() -> Seq(ValidationError("validate.error.expected.date.isoformat", pattern))))
      }
      case _ => JsError(Seq(JsPath() -> Seq(ValidationError("validate.error.expected.date"))))
    }

    private def parseDate(input: String): Option[java.util.Date] = {
      df.setLenient(false)
      try { Some(df.parse( input )) } catch {
        case _: java.text.ParseException => None
      }
    }

  }

}
