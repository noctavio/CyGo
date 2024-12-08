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

        // Initialize the game(as host) using the players in the lobby alongside whatever configuration.
        // 1, 3, 2, 4
        given()
                .when()
                .post("/lobby/1/initialize/game")
                .then()
                .statusCode(200);
    }
}
