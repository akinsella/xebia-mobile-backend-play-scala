package cloud

import play.api.mvc.Call
import play.api.libs.json.{Writes, Json}
import play.api.mvc.Results.{Ok, NotFound}

case class PagedContent(count: Int, pages: Int, page: Int)(urlToPage: (Int => Call))(implicit request: play.api.mvc.RequestHeader) {

  private val linkNextPage: String = """<%s>; rel="next""""
  private val linkLastPage: String = """<%s>; rel="last"""""
  private val linkFirstPage: String = """<%s>; rel="first""""
  private val linkPreviousPage: String = """<%s>; rel="prev"""""

  def getLinks: List[String] = {

    page match {

      case 1 => {
        List(
          linkNextPage.format(urlToPage(2).absoluteURL(false)),
          linkLastPage.format(urlToPage(pages).absoluteURL(false))
        )
      }

      case `pages` => {
        List(
          linkFirstPage.format(urlToPage(1).absoluteURL(false)),
          linkPreviousPage.format(urlToPage(pages - 1).absoluteURL(false))
        )
      }

      case x if (x < pages) => {
        List(
          linkFirstPage.format(urlToPage(1).absoluteURL(false)),
          linkPreviousPage.format(urlToPage(x - 1).absoluteURL(false)),
          linkNextPage.format(urlToPage(x + 1).absoluteURL(false)),
          linkLastPage.format(urlToPage(pages).absoluteURL(false))
        )
      }
    }
  }

}
