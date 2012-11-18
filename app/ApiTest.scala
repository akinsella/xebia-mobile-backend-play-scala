import play.test.FunctionalTest
import play.test.FunctionalTest._
import org.junit._

class ApiTests extends FunctionalTest {

    @Test
    def testGetWorkouts() {
        var response = GET("/api/workouts");
        assertStatus(200, response);
        assertContentType("application/json", response)
        println(response.out)
    }
}