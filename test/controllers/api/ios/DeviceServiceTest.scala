package controllers.api.ios

import controllers.api.routes
import org.joda.time.DateTime
import org.specs2.mutable.Specification
import play.api.http.ContentTypes.JSON
import play.api.http.HeaderNames.{CONTENT_TYPE, IF_MODIFIED_SINCE, LOCATION}
import play.api.libs.ws.{Response, WS}
import play.api.test.Helpers.{OK, CREATED, NOT_FOUND, NOT_MODIFIED}
import play.api.test.Helpers.{running, await, inMemoryDatabase}
import play.api.test._
import utils.RestHelper


class DeviceServiceTest extends Specification with RestHelper {


  val serverUrl = "http://localhost:3333%s"

  "api devices is UP and running" in {
    running(TestServer(3333, FakeApplication(additionalConfiguration = inMemoryDatabase()))) {

      //OPTIONS
      await(WS
        .url(serverUrl.format(routes.DeviceService.options().url))
        .options()
      ).status must equalTo(OK)

      //POST DEVICE
      val creationDevice =
        """{
          "udid": "1e2a3",
          "token":"toktok"
          } """

      val creationRequest = WS.url(serverUrl.format(routes.DeviceService.create().url))

      val creationResponse: Response = await(
        creationRequest
          .withHeaders(CONTENT_TYPE -> JSON)
          .post(creationDevice))

      creationResponse.status must equalTo(CREATED)
      val locationHeader = creationResponse.header(LOCATION)
      locationHeader.isDefined must beTrue

      //GET DEVICE BY ID
      val showResponse: Response = await(WS
        .url(locationHeader.get)
        .get())

      showResponse.status must equalTo(OK)

      (showResponse.json \ "udid").as[String] must equalTo("1e2a3")
      val idDevice = (showResponse.json \ "id").as[Long]
      val lastModified = new DateTime((showResponse.json \ "lastModified").as[Long])

      //GET WITH UP-TO-DATE CACHE
      val futureDate = lastModified.plusDays(1)

      val updateToDateCacheRequest = WS
        .url(locationHeader.get)
        .withHeaders((IF_MODIFIED_SINCE -> toHttpDate(futureDate.toDate)))
        .get()

      await(updateToDateCacheRequest).status must equalTo(NOT_MODIFIED)

      //PUT DEVICE
      val updateDevice =
        """{
          "udid": "1e2a3-updated",
          "token":"toktok"
          } """
      val updateRequest = WS.url(serverUrl.format(routes.DeviceService.save(idDevice)))

      await(
        updateRequest
          .withHeaders(CONTENT_TYPE -> JSON)
          .put(updateDevice)
      ).status must equalTo(OK)

      //GET DEVICES
      val getResponse: Response = await(WS
        .url(serverUrl.format(routes.DeviceService.devices().url))
        .get())

      getResponse.status must equalTo(OK)
      ((getResponse.json \\ "udid").map(_.as[String])) must containAnyOf(Seq("1e2a3-updated"))

      //DELETE DEVICE
      await(WS
        .url((serverUrl).format(routes.DeviceService.delete(idDevice).url))
        .delete()
      ).status must equalTo(OK)

      await(WS
        .url((serverUrl).format(routes.DeviceService.delete(idDevice).url)).delete()
      ).status must equalTo(NOT_FOUND)

      await(WS
        .url((serverUrl).format(routes.DeviceService.show(idDevice).url))
        .delete()
      ).status must equalTo(NOT_FOUND)

      await(WS
        .url((serverUrl).format(routes.DeviceService.save(idDevice).url))
        .withHeaders(CONTENT_TYPE -> JSON)
        .put(updateDevice)
      ).status must equalTo(NOT_FOUND)

      (await(WS
        .url(serverUrl.format(routes.DeviceService.devices().url))
        .get()
      ).json \\ "udid") must beEmpty


    }
  }


}
