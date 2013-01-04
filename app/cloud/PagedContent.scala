package cloud

import play.api.mvc.Call
import play.api.libs.json.{Writes, Json}
import play.api.mvc.Results.{Ok, NotFound}

case class PagedContent[T](content: Seq[T], pageSize: Int)(urlToPage: (Int => Call))(implicit request: play.api.mvc.RequestHeader, formatter: Writes[T]) {

  private val linkNextPage: String = """<%s>; rel="next""""
  private val linkLastPage: String = """<%s>; rel="last"""""
  private val linkFirstPage: String = """<%s>; rel="first""""
  private val linkPreviousPage: String = """<%s>; rel="prev"""""

  def getPage(page: Int) = {
    val numberOfPages = content.size / pageSize
    page match {
      case 1 => {
        if (numberOfPages != 1) {

          val pageLinkHeader = List(
            linkNextPage.format(urlToPage(2).absoluteURL(false)),
            linkLastPage.format(urlToPage(numberOfPages).absoluteURL(false))
          )

          Ok(Json.toJson(content.take(pageSize))).withHeaders(("Link", pageLinkHeader.mkString(",")))
        }
        else {
          Ok(Json.toJson(content))
        }
      }
      case `numberOfPages` => {
        val pageLinkHeader = List(
          linkFirstPage.format(urlToPage(1).absoluteURL(false)),
          linkPreviousPage.format(urlToPage(numberOfPages - 1).absoluteURL(false))
        )

        Ok(Json.toJson(content.takeRight(pageSize))).withHeaders(("Link", pageLinkHeader.mkString(",")))
      }
      case x if (x < numberOfPages) => {
        val pageLinkHeader = List(
          linkFirstPage.format(urlToPage(1).absoluteURL(false)),
          linkPreviousPage.format(urlToPage(x - 1).absoluteURL(false)),
          linkNextPage.format(urlToPage(x + 1).absoluteURL(false)),
          linkLastPage.format(urlToPage(numberOfPages).absoluteURL(false))
        )

        Ok(Json.toJson(content.slice((x - 1) * pageSize, x * pageSize))).withHeaders(("Link", pageLinkHeader.mkString(",")))
      }
      case _ => NotFound
    }
  }
}
