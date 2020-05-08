import dto.Content;
import io.qameta.allure.restassured.AllureRestAssured;
import io.restassured.RestAssured;
import io.restassured.builder.RequestSpecBuilder;
import io.restassured.http.ContentType;
import io.restassured.response.Response;
import io.restassured.specification.RequestSpecification;
import org.apache.http.HttpStatus;
import org.testng.Assert;
import org.testng.annotations.BeforeTest;
import org.testng.annotations.Test;

import static com.jayway.restassured.module.jsv.JsonSchemaValidator.matchesJsonSchemaInClasspath;
import static io.restassured.RestAssured.given;

public class CreateNewTaskTest {

    RequestSpecification requestSpec = new RequestSpecBuilder()
            .setBaseUri("https://api.todoist.com")
            .addHeader("Authorization", "Bearer 8ac3f173ac0b530f7f77d4858d7e41ce801bdc26")
            .setContentType(ContentType.JSON)
            .setBasePath("/rest/v1/tasks")
            .build()
            .filter(new AllureRestAssured());

    @BeforeTest
    public void setFilter() {
        RestAssured.filters(new AllureRestAssured());
    }

    @Test
    public void checkResponseBodyMatchesCreateNewTaskSchema() {
        Content content = Content.builder()
                .content("Appointment with Maria")
                .due_string("tomorrow at 12:00")
                .due_lang("en")
                .priority(1)
                .build();
        given()
                .spec(requestSpec)
                .body(content)
                .when()
                .post()
                .then()
                .log()
                .body()
                .body(matchesJsonSchemaInClasspath("createNewTaskSchema.json"));
    }

    @Test
    public void checkStatusCodeWithoutContentRequiredParameter() {
        Content content = Content.builder()
                .content("")
                .due_string("tomorrow at 12:00")
                .due_lang("en")
                .priority(1)
                .build();
        given()
                .spec(requestSpec)
                .body(content)
                .when()
                .post()
                .then()
                .assertThat()
                .statusCode(HttpStatus.SC_BAD_REQUEST);
    }

    @Test
    public void checkResponseWhenParamPriorityIsLessThenRequired() {
        Content content = Content.builder()
                .content("Appointment with Maria")
                .due_string("tomorrow at 12:00")
                .due_lang("en")
                .priority(0)
                .build();
        Response response = given()
                .spec(requestSpec)
                .body(content)
                .when()
                .post();
        String bodyStringValue = response.getBody().asString();
        Assert.assertEquals(bodyStringValue,"Unsupported priority value",
                "Message isn't equal expected,but should");
        response.then()
                .assertThat()
                .statusCode(HttpStatus.SC_BAD_REQUEST);
    }

    @Test
    public void checkResponseWhenParamPriorityIsGreaterThenRequired() {
        Content content = Content.builder()
                .content("Appointment with Maria")
                .due_string("tomorrow at 12:00")
                .due_lang("en")
                .priority(5)
                .build();
        Response response = given()
                .spec(requestSpec)
                .body(content)
                .when()
                .post();
        String bodyStringValue = response.getBody().asString();
        Assert.assertEquals(bodyStringValue,"Unsupported priority value",
                "Message isn't equal expected,but should");
        response.then()
                .assertThat()
                .statusCode(HttpStatus.SC_BAD_REQUEST);
    }

    @Test
    public void checkResponseWhenParamPriorityWithInvalidValue() {
        Content content = Content.builder()
                .content("Appointment with Maria")
                .due_string("tomorrow at 12:00")
                .due_lang("en")
                .priority(-1)
                .build();
        Response response = given()
                .spec(requestSpec)
                .body(content)
                .when()
                .post();
        String bodyStringValue = response.getBody().asString();
        Assert.assertEquals(bodyStringValue,"JSON decode error: unexpected number -1 at pos 104",
                "Message isn't equal expected,but should");
        response.then()
                .assertThat()
                .statusCode(HttpStatus.SC_BAD_REQUEST);
    }
}