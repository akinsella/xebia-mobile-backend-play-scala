package controllers.api

import org.specs2.mutable.Specification

import play.api.libs.ws.{Response, WS}
import play.api.test._
import play.api.test.Helpers.{running, await,inMemoryDatabase}
import play.api.test.Helpers.{OK, CREATED, NOT_FOUND}

class DeviceServiceTest extends Specification {


  val serverUrl = "http://localhost:3333%s"

  "api devices is UP and running" in {
    running(TestServer(3333,FakeApplication(additionalConfiguration = inMemoryDatabase()))) {

      //OPTIONS
      await(WS.url(serverUrl.format(routes.DeviceService.options().url)).options()).status must equalTo(OK)

      //POST DEVICE
      val creationDevice =
        """{
          "udid": "1e2a3",
          "token":"toktok"
          } """

      val creationQuery = WS.url(serverUrl.format(routes.DeviceService.create().url))

      val creationResponse: Response = await(creationQuery
        .withHeaders("Content-Type" -> "application/json")
        .post(creationDevice))

      creationResponse.status must equalTo(CREATED)
      val locationHeader = creationResponse.header("Location")
      locationHeader.isDefined must beTrue


      //GET DEVICE BY ID
      val showResponse: Response = await(WS.url(serverUrl.format(locationHeader.get)).get())
      showResponse.status must equalTo(OK)
      (showResponse.json \ "udid").as[String] must equalTo("1e2a3")
      val idDevice = (showResponse.json \ "id").as[Long]


      //PUT DEVICE
      val updateDevice =
        """{
          "udid": "1e2a3-updated",
          "token":"toktok"
          } """
      val updateQuery = WS.url(serverUrl.format(routes.DeviceService.save(idDevice)))
      val updateResponse = await(updateQuery
        .withHeaders("Content-Type" -> "application/json")
        .put(updateDevice))

      updateResponse.status must equalTo(OK)

      //GET DEVICES
      val getResponse: Response = await(WS.url(serverUrl.format(routes.DeviceService.devices().url)).get())
      getResponse.status must equalTo(OK)
      ((getResponse.json \\ "udid").map(_.as[String])) must containAnyOf(Seq("1e2a3-updated"))

      //DELETE DEVICE
      await(WS.url((serverUrl).format(routes.DeviceService.delete(idDevice).url)).delete()).status must equalTo(OK)


      await(WS.url((serverUrl).format(routes.DeviceService.delete(idDevice).url)).delete()).status must equalTo(NOT_FOUND)
      await(WS.url((serverUrl).format(routes.DeviceService.show(idDevice).url)).delete()).status must equalTo(NOT_FOUND)
      await(WS.url((serverUrl).format(routes.DeviceService.save(idDevice).url))
        .withHeaders("Content-Type" -> "application/json")
        .put(updateDevice)
      ).status must equalTo(NOT_FOUND)

      (await(WS.url(serverUrl.format(routes.DeviceService.devices().url)).get()).json \\ "udid") must beEmpty


    }
  }


}
