package com.example.sb;

import static io.restassured.RestAssured.given;
import static org.hamcrest.Matchers.equalTo;

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
public class QuickGameTest {

    @LocalServerPort
    private int port;

    @Before
    public void setup() {
        // Correctly set the base URI and port
        RestAssured.baseURI = "http://localhost";
        RestAssured.port = port;
    }

    @Test
    public void basicQuickTest() {
        String user1JSON = "{\"username\": \"Dummy1\", \"password\": \"password1\"}, \"isAdmin\": \"0\"}";
        String user2JSON = "{\"username\": \"Dummy2\", \"password\": \"password2\"}, \"isAdmin\": \"0\"}";
        String user3JSON = "{\"username\": \"Dummy3\", \"password\": \"password3\"}, \"isAdmin\": \"0\"}";
        String user4JSON = "{\"username\": \"Dummy4\", \"password\": \"password4\"}, \"isAdmin\": \"0\"}";
        String user5JSON = "{\"username\": \"Dummy5\", \"password\": \"password5\"}, \"isAdmin\": \"0\"}";
        // TODO use later to test timer and such.
        String newConfigJSON = "{ \"gameTime\": \"25\", \"hostName\": \"Dummy3\", \"isFriendly\": \"false\" }";

        //Register 5 Dummies(TODO use 5 later for test.)
        given()
                .contentType(ContentType.JSON)
                .body(user1JSON)
                .when()
                .post("/users/register").then()
                .statusCode(200);
        given()
                .contentType(ContentType.JSON)
                .body(user2JSON)
                .when()
                .post("/users/register").then()
                .statusCode(200);
        given()
                .contentType(ContentType.JSON)
                .body(user3JSON)
                .when()
                .post("/users/register").then()
                .statusCode(200);
        given()
                .contentType(ContentType.JSON)
                .body(user4JSON)
                .when()
                .post("/users/register").then()
                .statusCode(200);
        given()
                .contentType(ContentType.JSON)
                .body(user5JSON)
                .when()
                .post("/users/register").then()
                .statusCode(200);

        // Make Dummy1 host and initialize a lobby
        given()
                .when()
                .post("/lobby/1/create")
                .then()
                .statusCode(200);

        // Make the other three join and then set their status to ready.
        given()
                .when()
                .put("/lobby/2/join/1")
                .then()
                .statusCode(500); // TODO EXCLAIMER, FK issue where the second person to join the lobby will raise a false positive on a 'duplicate entry'.
        given()
                .when()
                .put("/lobby/3/join/1")
                .then()
                .statusCode(200);
        given()
                .when()
                .put("/lobby/4/join/1")
                .then()
                .statusCode(200);

        // MAKE ALL PLAYERS SET READY, START GAME, END GAME, ELO TESTING
        given()
                .when()
                .put("/lobby/players/1/toggleReady")
                .then()
                .statusCode(200);
        given()
                .when()
                .put("/lobby/players/2/toggleReady")
                .then()
                .statusCode(200);
        given()
                .when()
                .put("/lobby/players/3/toggleReady")
                .then()
                .statusCode(200);
        given()
                .when()
                .put("/lobby/players/4/toggleReady")
                .then()
                .statusCode(200);
    }
}
