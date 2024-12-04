package com.example.sb;

import static io.restassured.RestAssured.given;
import static org.hamcrest.Matchers.*;

import io.restassured.http.ContentType;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.test.context.junit4.SpringRunner;

import io.restassured.RestAssured;

import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.web.server.LocalServerPort;


@RunWith(SpringRunner.class)
@SpringBootTest(webEnvironment = SpringBootTest.WebEnvironment.RANDOM_PORT)
public class OctavioTests {

    @LocalServerPort
    private int port;

    @Before
    public void setup() {
        // Correctly set the base URI and port
        RestAssured.baseURI = "http://localhost";
        RestAssured.port = port;
    }

    @Test
    public void registerLoginAndLogoutTest() {
        String requestBody = "{\"username\": \"Octavio\", \"password\": \"kelpTree12\"}";

        //Register
        given()
                .contentType(ContentType.JSON)
                    .body(requestBody)
                .when()
                    .post("/users/register").then()
                .statusCode(200);
        //Verify created user.
        given()
                .when()
                .get("/users/Octavio")
                .then()
                .statusCode(200)
                .contentType(ContentType.JSON)
                .body("username", equalTo("Octavio"))
                .body("password", notNullValue())
                .body("password", not(emptyString()))
                .body("password", matchesPattern("^[A-Za-z0-9$./]{60}$"));
        //Login
        given()
                .when()
                .put("/users/login/Octavio/kelpTree12")
                .then()
                .statusCode(200);
        given()
                .when()
                .get("/users/Octavio")
                .then()
                .statusCode(200)
                .contentType(ContentType.JSON)
                .body("isLoggedIn", equalTo(true));
        //Logout
        given()
                .when()
                .put("/users/logout/1")
                .then()
                .statusCode(200);
        given()
                .when()
                .get("/users/Octavio")
                .then()
                .statusCode(200)
                .contentType(ContentType.JSON)
                .body("isLoggedIn", equalTo(false));
    }
}
