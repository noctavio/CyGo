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
public class QuickTest {

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
            // MAKE ALL PLAYERS SET READY
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

        // VERIFY SCORING!
        given()
                .when()
                .get("/lobby/teams/1")
                .then()
                .contentType(ContentType.JSON)
                .body("[0].teamCaptures", equalTo(0.0F));
        given()
                .when()
                .get("/lobby/teams/1")
                .then()
                .contentType(ContentType.JSON)
                .body("[1].teamCaptures", equalTo(0.0F));
        //VERIFY SCORING!

        // Set of turns
        given()
                .when()
                .post("/goban/1/place/4/4") //TODO BLACK
                .then()
                .statusCode(200);
        given()
                .when()
                .post("/goban/3/place/3/6")// TODO WHITE
                .then()
                .statusCode(200);
        given()
                .when()
                .post("/goban/2/place/5/6")//TODO BLACK
                .then()
                .statusCode(200);
        given()
                .when()
                .post("/goban/4/place/2/4")// TODO WHITE
                .then()
                .statusCode(200);
        // Set of turns end

        // Set of turns
        given()
                .when()
                .post("/goban/1/place/2/3")//TODO BLACK
                .then()
                .statusCode(200);
        given()
                .when()
                .post("/goban/3/place/1/3")// TODO WHITE
                .then()
                .statusCode(200);
        given()
                .when()
                .post("/goban/2/place/3/3")//TODO BLACK
                .then()
                .statusCode(200);
        given()
                .when()
                .post("/goban/4/place/6/2")// TODO WHITE
                .then()
                .statusCode(200);
        // Set of turns end

        // Set of turns
        given()
                .when()
                .post("/goban/1/place/2/5")//TODO BLACK
                .then()
                .statusCode(200);
        given()
                .when()
                .post("/goban/3/place/3/5")// TODO WHITE
                .then()
                .statusCode(200);
        given()
                .when()
                .post("/goban/2/place/1/4")//TODO BLACK
                .then()
                .statusCode(200);
        given()
                .when()
                .post("/goban/4/place/3/4")// TODO WHITE
                .then()
                .statusCode(200);
        // Set of turns end

        // Set of turns
        given()
                .when()
                .post("/goban/1/place/1/2")//TODO BLACK
                .then()
                .statusCode(200);
        given()
                .when()
                .post("/goban/3/place/1/5")// TODO WHITE
                .then()
                .statusCode(200);
        given()
                .when()
                .post("/goban/2/place/0/3")//TODO BLACK
                .then()
                .statusCode(200);

        // VERIFY SCORING!
        given()
                .when()
                .get("/lobby/teams/1")
                .then()
                .contentType(ContentType.JSON)
                .body("[0].teamCaptures", equalTo(1.0F));
        given()
                .when()
                .get("/lobby/teams/1")
                .then()
                .contentType(ContentType.JSON)
                .body("[1].teamCaptures", equalTo(0.0F));
        //VERIFY SCORING!

        given()
                .when()
                .post("/goban/4/place/2/6")// TODO WHITE
                .then()
                .statusCode(200);

        // VERIFY SCORING!
        given()
                .when()
                .get("/lobby/teams/1")
                .then()
                .contentType(ContentType.JSON)
                .body("[0].teamCaptures", equalTo(1.0F));
        given()
                .when()
                .get("/lobby/teams/1")
                .then()
                .contentType(ContentType.JSON)
                .body("[1].teamCaptures", equalTo(1.0F));
        //VERIFY SCORING!

        // Set of turns end

        // Set of turns
        given()
                .when()
                .post("/goban/1/place/6/4")//TODO BLACK
                .then()
                .statusCode(200);
        given()
                .when()
                .post("/goban/3/place/4/1")// TODO WHITE
                .then()
                .statusCode(200);
        given()
                .when()
                .post("/goban/2/place/3/1 ")//TODO BLACK
                .then()
                .statusCode(200);
        given()
                .when()
                .post("/goban/4/place/4/2")// TODO WHITE
                .then()
                .statusCode(200);
        // Set of turns end

        // Set of turns
        given()
                .when()
                .post("/goban/1/place/7/3")//TODO BLACK
                .then()
                .statusCode(200);
        given()
                .when()
                .post("/goban/3/place/7/2")// TODO WHITE
                .then()
                .statusCode(200);
        given()
                .when()
                .post("/goban/2/place/4/7")//TODO BLACK
                .then()
                .statusCode(200);
        given()
                .when()
                .post("/goban/4/place/3/7")// TODO WHITE
                .then()
                .statusCode(200);
        // Set of turns end

        // Set of turns
        given()
                .when()
                .post("/goban/1/place/5/3")//TODO BLACK
                .then()
                .statusCode(200);
        given()
                .when()
                .post("/goban/3/place/3/2")// TODO WHITE
                .then()
                .statusCode(200);
        given()
                .when()
                .post("/goban/2/place/2/1")//TODO BLACK
                .then()
                .statusCode(200);
        given()
                .when()
                .post("/goban/4/place/6/3")// TODO WHITE
                .then()
                .statusCode(200);
        // Set of turns end

        // Set of turns
        given()
                .when()
                .post("/goban/1/place/5/2")//TODO BLACK
                .then()
                .statusCode(200);
        given()
                .when()
                .post("/goban/3/place/7/4")// TODO WHITE
                .then()
                .statusCode(200);
        given()
                .when()
                .post("/goban/2/place/7/5")//TODO BLACK
                .then()
                .statusCode(200);
        given()
                .when()
                .post("/goban/4/place/8/3")// TODO WHITE
                .then()
                .statusCode(200);

        // VERIFY SCORING!
        given()
                .when()
                .get("/lobby/teams/1")
                .then()
                .contentType(ContentType.JSON)
                .body("[0].teamCaptures", equalTo(1.0F));
        given()
                .when()
                .get("/lobby/teams/1")
                .then()
                .contentType(ContentType.JSON)
                .body("[1].teamCaptures", equalTo(2.0F));
        //VERIFY SCORING!


        // Set of turns end

        // Set of turns
        given()
                .when()
                .post("/goban/1/place/5/1")//TODO BLACK
                .then()
                .statusCode(200);
        given()
                .when()
                .post("/goban/3/place/6/5")// TODO WHITE
                .then()
                .statusCode(200);
        given()
                .when()
                .post("/goban/2/place/5/4")//TODO BLACK
                .then()
                .statusCode(200);
        given()
                .when()
                .post("/goban/4/place/7/6")// TODO WHITE
                .then()
                .statusCode(200);
        // Set of turns end

        // Set of turns
        given()
                .when()
                .post("/goban/1/place/6/6")//TODO BLACK
                .then()
                .statusCode(200);
        given()
                .when()
                .post("/goban/3/place/8/5")// TODO WHITE
                .then()
                .statusCode(200);

        // VERIFY SCORING!
        given()
                .when()
                .get("/lobby/teams/1")
                .then()
                .contentType(ContentType.JSON)
                .body("[0].teamCaptures", equalTo(1.0F));
        given()
                .when()
                .get("/lobby/teams/1")
                .then()
                .contentType(ContentType.JSON)
                .body("[1].teamCaptures", equalTo(3.0F));
        //VERIFY SCORING!

        given()
                .when()
                .post("/goban/2/place/7/7")//TODO BLACK
                .then()
                .statusCode(200);
        given()
                .when()
                .post("/goban/4/place/8/7")// TODO WHITE
                .then()
                .statusCode(200);
        // Set of turns end

        // Set of turns
        given()
                .when()
                .post("/goban/1/place/6/7")//TODO BLACK
                .then()
                .statusCode(200);
        given()
                .when()
                .post("/goban/3/place/5/5")// TODO WHITE
                .then()
                .statusCode(200);
        given()
                .when()
                .post("/goban/2/place/4/5")//TODO BLACK
                .then()
                .statusCode(200);
        given()
                .when()
                .post("/goban/4/place/4/6")// TODO WHITE
                .then()
                .statusCode(200);
        // Set of turns end

        // Set of turns
        given()
                .when()
                .post("/goban/1/place/7/5")//TODO BLACK
                .then()
                .statusCode(200);

        // VERIFY SCORING!
        given()
                .when()
                .get("/lobby/teams/1")
                .then()
                .contentType(ContentType.JSON)
                .body("[0].teamCaptures", equalTo(3.0F));
        given()
                .when()
                .get("/lobby/teams/1")
                .then()
                .contentType(ContentType.JSON)
                .body("[1].teamCaptures", equalTo(3.0F));
        //VERIFY SCORING!

        given()
                .when()
                .post("/goban/3/place/6/5")// TODO WHITE
                .then()
                .statusCode(200);

        given()
                .when()
                .get("/lobby/teams/1")
                .then()
                .contentType(ContentType.JSON)
                .body("[0].teamCaptures", equalTo(3.0F));
        given()
                .when()
                .get("/lobby/teams/1")
                .then()
                .contentType(ContentType.JSON)
                .body("[1].teamCaptures", equalTo(4.0F));

        given()
                .when()
                .post("/goban/2/place/5/5")//TODO BLACK
                .then()
                .statusCode(200);
        given()
                .when()
                .post("/goban/4/place/7/5")// TODO WHITE
                .then()
                .statusCode(200);
        // Set of turns end

        // Set of turns
        given()
                .when()
                .post("/goban/1/place/6/1")//TODO BLACK
                .then()
                .statusCode(200);
        given()
                .when()
                .post("/goban/3/place/7/1")// TODO WHITE
                .then()
                .statusCode(200);
        given()
                .when()
                .post("/goban/2/place/3/8")//TODO BLACK
                .then()
                .statusCode(200);
        given()
                .when()
                .post("/goban/4/place/2/8")// TODO WHITE
                .then()
                .statusCode(200);
        // Set of turns end

        // Set of turns
        given()
                .when()
                .post("/goban/1/place/4/8")//TODO BLACK
                .then()
                .statusCode(200);
        given()
                .when()
                .post("/goban/3/place/0/5")// TODO WHITE
                .then()
                .statusCode(200);
        given()
                .when()
                .post("/goban/2/place/0/4")//TODO BLACK
                .then()
                .statusCode(200);
        given()
                .when()
                .post("/goban/4/place/7/0")// TODO WHITE
                .then()
                .statusCode(200);
        // Set of turns end

        // Set of turns
        given()
                .when()
                .post("/goban/1/place/6/0")//TODO BLACK
                .then()
                .statusCode(200);
        given()
                .when()
                .post("/goban/3/place/7/8")// TODO WHITE
                .then()
                .statusCode(200);
        given()
                .when()
                .post("/goban/2/place/6/8")//TODO BLACK
                .then()
                .statusCode(200);
        given()
                .when()
                .post("/goban/4/place/8/6")// TODO WHITE
                .then()
                .statusCode(200);
        // Set of turns end

        // Set of turns
        given()
                .when()
                .post("/goban/1/place/6/0")//TODO BLACK
                .then()
                .statusCode(200);
        given()
                .when()
                .post("/goban/3/place/7/8")// TODO WHITE
                .then()
                .statusCode(200);
        given()
                .when()
                .post("/goban/2/place/6/8")//TODO BLACK
                .then()
                .statusCode(200);
        given()
                .when()
                .post("/goban/4/place/8/6")// TODO WHITE
                .then()
                .statusCode(200);
        // Set of turns end

        // Set of turns
        given()
                .when()
                .post("/goban/1/place/8/8")//TODO BLACK
                .then()
                .statusCode(200);

        given()
                .when()
                .get("/lobby/teams/1")
                .then()
                .contentType(ContentType.JSON)
                .body("[0].teamCaptures", equalTo(4.0F));
        given()
                .when()
                .get("/lobby/teams/1")
                .then()
                .contentType(ContentType.JSON)
                .body("[1].teamCaptures", equalTo(4.0F));

        given()
                .when()
                .post("/goban/3/place/5/7")// TODO WHITE
                .then()
                .statusCode(200);
        given()
                .when()
                .post("/goban/2/place/5/8")//TODO BLACK
                .then()
                .statusCode(200);

        given()
                .when()
                .get("/lobby/teams/1")
                .then()
                .contentType(ContentType.JSON)
                .body("[0].teamCaptures", equalTo(5.0F));
        given()
                .when()
                .get("/lobby/teams/1")
                .then()
                .contentType(ContentType.JSON)
                .body("[1].teamCaptures", equalTo(4.0F));

        given()
                .when()
                .post("/goban/4/place/7/8")// TODO WHITE
                .then()
                .statusCode(200);

        given()
                .when()
                .get("/lobby/teams/1")
                .then()
                .contentType(ContentType.JSON)
                .body("[0].teamCaptures", equalTo(5.0F));
        given()
                .when()
                .get("/lobby/teams/1")
                .then()
                .contentType(ContentType.JSON)
                .body("[1].teamCaptures", equalTo(5.0F));

        // Set of turns end

        // Set of turns
        given()
                .when()
                .post("/goban/1/place/2/7")//TODO BLACK
                .then()
                .statusCode(200);
        given()
                .when()
                .post("/goban/3/place/1/7")// TODO WHITE
                .then()
                .statusCode(200);

        given()
                .when()
                .get("/lobby/teams/1")
                .then()
                .contentType(ContentType.JSON)
                .body("[0].teamCaptures", equalTo(5.0F));
        given()
                .when()
                .get("/lobby/teams/1")
                .then()
                .contentType(ContentType.JSON)
                .body("[1].teamCaptures", equalTo(6.0F));

        given()
                .when()
                .post("/goban/2/place/8/8")//TODO BLACK
                .then()
                .statusCode(200);

        given()
                .when()
                .get("/lobby/teams/1")
                .then()
                .contentType(ContentType.JSON)
                .body("[0].teamCaptures", equalTo(6.0F));
        given()
                .when()
                .get("/lobby/teams/1")
                .then()
                .contentType(ContentType.JSON)
                .body("[1].teamCaptures", equalTo(6.0F));

        given()
                .when()
                .post("/goban/4/place/0/2")// TODO WHITE
                .then()
                .statusCode(200);
        // Set of turns end

        // Set of turns
        given()
                .when()
                .post("/goban/1/place/0/1")//TODO BLACK
                .then()
                .statusCode(200);

        given()
                .when()
                .get("/lobby/teams/1")
                .then()
                .contentType(ContentType.JSON)
                .body("[0].teamCaptures", equalTo(7.0F));
        given()
                .when()
                .get("/lobby/teams/1")
                .then()
                .contentType(ContentType.JSON)
                .body("[1].teamCaptures", equalTo(6.0F));

        given()
                .when()
                .post("/goban/3/place/7/8")
                .then()
                .statusCode(200);

        given()
                .when()
                .get("/lobby/teams/1")
                .then()
                .contentType(ContentType.JSON)
                .body("[0].teamCaptures", equalTo(7.0F));
        given()
                .when()
                .get("/lobby/teams/1")
                .then()
                .contentType(ContentType.JSON)
                .body("[1].teamCaptures", equalTo(7.0F));

        given()
                .when()
                .post("/goban/2/place/1/8")//TODO BLACK
                .then()
                .statusCode(200);
        given()
                .when()
                .post("/goban/4/place/0/8")// TODO WHITE
                .then()
                .statusCode(200);

        given()
                .when()
                .get("/lobby/teams/1")
                .then()
                .contentType(ContentType.JSON)
                .body("[0].teamCaptures", equalTo(7.0F));
        given()
                .when()
                .get("/lobby/teams/1")
                .then()
                .contentType(ContentType.JSON)
                .body("[1].teamCaptures", equalTo(8.0F));

        // Set of turns end

        // Set of turns
        given()
                .when()
                .post("/goban/1/place/8/8")//TODO BLACK
                .then()
                .statusCode(200);

        given()
                .when()
                .get("/lobby/teams/1")
                .then()
                .contentType(ContentType.JSON)
                .body("[0].teamCaptures", equalTo(8.0F));
        given()
                .when()
                .get("/lobby/teams/1")
                .then()
                .contentType(ContentType.JSON)
                .body("[1].teamCaptures", equalTo(8.0F));

        given()
                .when()
                .put("/goban/3/pass")
                .then()
                .statusCode(200);
        given()
                .when()
                .post("/goban/2/place/7/8")//TODO BLACK
                .then()
                .statusCode(200);

        given()// TODO FINAL VERIFICATION OCCURS HERE, no change!
                .when()
                .get("/lobby/teams/1")
                .then()
                .contentType(ContentType.JSON)
                .body("[0].teamCaptures", equalTo(8.0F));
        given()
                .when()
                .get("/lobby/teams/1")
                .then()
                .contentType(ContentType.JSON)
                .body("[1].teamCaptures", equalTo(8.0F));

        given()
                .when()
                .put("/goban/4/pass")// TODO WHITE
                .then()
                .statusCode(200);
        given()
                .when()
                .put("/goban/1/pass") //TODO BLACK
                .then()
                .statusCode(200);
        given()
                .when()
                .put("/goban/3/pass")// TODO WHITE
                .then()
                .statusCode(200);

    }
}
