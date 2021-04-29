package ctie.dmf;

import io.quarkus.test.junit.QuarkusTest;
import org.junit.jupiter.api.Test;

import ctie.dmf.entity.Appellation;

import static io.restassured.RestAssured.given;
import static org.hamcrest.CoreMatchers.is;

@QuarkusTest
public class RestAPITest {

    @Test
    public void testAppellationEndPoint() {
        given()
          .when().get("/appellations/v1")
          .then()
             .statusCode(200)
             .body(is("[]"));
    }

}