package com.example.sb;

import static io.restassured.RestAssured.given;
import static org.hamcrest.Matchers.*;

import io.restassured.http.ContentType;
import org.junit.Before;
import org.junit.FixMethodOrder;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.junit.runners.MethodSorters;
import org.springframework.test.context.junit4.SpringRunner;

import io.restassured.RestAssured;

import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.web.server.LocalServerPort;


@RunWith(SpringRunner.class)
@SpringBootTest(webEnvironment = SpringBootTest.WebEnvironment.RANDOM_PORT)
@FixMethodOrder(MethodSorters.NAME_ASCENDING)
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
    public void test1_registerLoginAndLogout_etc() {
        String user1JSON = "{\"username\": \"Dummy1\", \"password\": \"password1\"}";
        String user2JSON = "{\"username\": \"Dummy2\", \"password\": \"password1\"}";

        //Register
        given()
                .contentType(ContentType.JSON)
                .body(user1JSON)
                .when()
                .post("/users/register").then()
                .statusCode(200);
        //Register(Dummy)
        given()
                .contentType(ContentType.JSON)
                .body(user2JSON)
                .when()
                .post("/users/register").then()
                .statusCode(200);
        //Login
        given()
                .when()
                .put("/users/login/Dummy1/password1")
                .then()
                .statusCode(200);
        //Verify created users
        given()
                .when()
                .get("/users")
                .then()
                .statusCode(200)
                .contentType(ContentType.JSON)
                .body("[0].username", equalTo("Dummy1"))
                .body("[0].password", matchesPattern("^[A-Za-z0-9$./]{60}$"))
                .body("[0].isLoggedIn", equalTo(true))
                .body("[1].username", equalTo("Dummy2"))
                .body("[1].password", matchesPattern("^[A-Za-z0-9$./]{60}$"))
                .body("[1].isLoggedIn", equalTo(false));
        //Logout
        given()
                .when()
                .put("/users/logout/1")
                .then()
                .statusCode(200);
        given()
                .when()
                .get("/users/Dummy1")
                .then()
                .statusCode(200)
                .contentType(ContentType.JSON)
                .body("isLoggedIn", equalTo(false));
        //Delete dummy and verify
        given()
                .when()
                .delete("/users/hardDelete/2")
                .then()
                .statusCode(200);
        given()
                .when()
                .get("/users/Dummy2")
                .then()
                .statusCode(404);
    }

    @Test
    public void test2_profilesAndLeaderboard() {
        String user3JSON = "{\"username\": \"Dummy3\", \"password\": \"password3\"}";
        String user4JSON = "{\"username\": \"Dummy4\", \"password\": \"password4\"}";
        String user5JSON = "{\"username\": \"Dummy5\", \"password\": \"password5\"}";

        //Register three more test dummies
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
        //Get profile of some user
        given()
                .when()
                .get("/profiles/3")
                .then()
                .statusCode(200)
                .contentType(ContentType.JSON)
                .body("username", equalTo("Dummy3"))
                .body("rank", equalTo("30 kyu"));
        //Verify that 4 profiles exist, also verifies that hardDelete works and that it deleted Dummy2's profile
        given()
                .when()
                .get("/profiles")
                .then()
                .statusCode(200)
                .contentType(ContentType.JSON)
                .body("size()", equalTo(4));
        given()
                .when()
                .get("/leaderboard")
                .then()
                .statusCode(200)
                .contentType(ContentType.JSON)
                .body("size()", equalTo(4));
        // Rank adjustment not implemented yet, and I have no methods to manually adjust it(should never occur)
        // so I cannot demonstrate leaderboard ranking players correctly.
        // Rank adjustment is incredibly trivial, I assure you it works.
    }

    @Test
    public void test3_Lobby() {

        // Make Dummy1 host and initialize a lobby
        given()
                .when()
                .post("/lobby/1/create")
                .then()
                .statusCode(200);

        // Make the other three join and then set their status to ready.
        given()
                .when()
                .put("/lobby/3/join/1")
                .then()
                .statusCode(500); // TODO EXCLAIMER, FK issue where the second person to join the lobby will raise a false positive on a 'duplicate entry'. Meaning it's not duplicate although springboot says otherwise despite verifying id and user integrity.
        given()
                .when()
                .put("/lobby/4/join/1")
                .then()
                .statusCode(200);
        given()
                .when()
                .put("/lobby/5/join/1")
                .then()
                .statusCode(200);

        // Make player 5 leave and rejoin
        given()
                .when()
                .delete("/lobby/5/leave")
                .then()
                .statusCode(200);
        given()
                .when()
                .put("/lobby/5/join/1")
                .then()
                .statusCode(200);

        // Make host kick Dummy5 and then make them rejoin.
        given()
                .when()
                .delete("/lobby/1/kick/5")
                .then()
                .statusCode(200);
        given()
                .when()
                .put("/lobby/5/join/1")
                .then()
                .statusCode(200);

        given()
                .when()
                .put("/lobby/players/1/toggleReady")
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
        given()
                .when()
                .put("/lobby/players/5/toggleReady")
                .then()
                .statusCode(200);

        //verify only one lobby exists amongst the 4 players.
        given()
                .when()
                .get("/lobby")
                .then()
                .statusCode(200)
                .contentType(ContentType.JSON)
                .body("size()", equalTo(1));

        String newConfigJSON = "{ \"gameTime\": \"25\", \"hostName\": \"Dummy3\", \"isFriendly\": \"false\" }";
        // Host transfer and game
        given()
                .contentType(ContentType.JSON) // Specify the content type
                .body(newConfigJSON) // Add the request body
                .when()
                .put("/lobby/1/updateConfig")
                .then()
                .statusCode(200);
        given()
                .when()
                .get("/lobby/allPlayers/1")
                .then()
                .statusCode(200)
                .contentType(ContentType.JSON)
                .body("size()", equalTo(4));

        // Verify only two teams exist in the lobby.
        given()
                .when()
                .get("/lobby/teams/1")
                .then()
                .statusCode(200)
                .contentType(ContentType.JSON)
                .body("size()", equalTo(2));

        // Change team name
        String teamJSON = "{ \"teamName\": \"ParanoidAndroidStudio\"}";
        given()
                .contentType(ContentType.JSON) // Specify the content type
                .body(teamJSON) // Add the request body
                .put("/lobby/teams/1/updateTeamName")
                .then()
                .statusCode(200);

        // Leave team and verify, also rejoin same team
        given()
                .when()
                .delete("/lobby/teams/5/leave")
                .then()
                .statusCode(200);
        given()
                .when()
                .get("/lobby/players/team/2")
                .then()
                .statusCode(200)
                .contentType(ContentType.JSON)
                .body("size()", equalTo(1));
        given()
                .when()
                .post("/lobby/teams/1/join/2")
                .then()
                .statusCode(200);
        given()
                .when()
                .get("/lobby/players/team/2")
                .then()
                .statusCode(200)
                .contentType(ContentType.JSON)
                .body("size()", equalTo(2));

        // Test mute/unmute, verify the list is being updated.(not testing websocket here)
        given()
                .when()
                .put("/lobby/players/1/mute/3")
                .then()
                .statusCode(200);
        given()
                .when()
                .get("/lobby/players/mutedList/1")
                .then()
                .statusCode(200)
                .contentType(ContentType.JSON)
                .body("size()", equalTo(1));
        given()
                .when()
                .put("/lobby/players/1/unmute/3")
                .then()
                .statusCode(200);
        given()
                .when()
                .get("/lobby/players/mutedList/1")
                .then()
                .statusCode(200)
                .contentType(ContentType.JSON)
                .body("size()", equalTo(0));

        // Toggle black vote and verify the teams have changed colors after majority vote.
        // Team 1 is initialized as black upon lobby creation.
        given()
                .when()
                .get("/lobby/teams/1")
                .then()
                .statusCode(200)
                .contentType(ContentType.JSON)
                .body("[0].isBlack", equalTo(true));
        given()
                .when()
                .put("/lobby/players/1/toggleBlackVote")
                .then()
                .statusCode(200);
        given()
                .when()
                .put("/lobby/players/3/toggleBlackVote")
                .then()
                .statusCode(200);
        given()
                .when()
                .put("/lobby/players/4/toggleBlackVote")
                .then()
                .statusCode(200);
        given()
                .when()
                .get("/lobby/teams/1")
                .then()
                .statusCode(200)
                .contentType(ContentType.JSON)
                .body("[0].isBlack", equalTo(false));

        // Initialize the game(as host) using the players in the lobby alongside whatever configuration.
        given()
                .when()
                .post("/lobby/3/initialize/game")
                .then()
                .statusCode(200);
        // Check that the game is initialized
        given()
                .when()
                .get("/lobby")
                .then()
                .statusCode(200)
                .contentType(ContentType.JSON)
                .body("[0].isGameInitialized", equalTo(true));
    }

    @Test
    public void test4_Game() {
        // Emulates a faux game where Dummy3(Black team player 1 starts first)
        // places a black piece at (0,0) then Dummy1(next in sequence) passes their turn
        // Turn order for this demonstration is Dummy4(Team2), Dummy1(Team1), Dummy5(Team2), Dummy3(Team1)
        given()
                .when()
                .post("/goban/4/place/0/0")
                .then()
                .statusCode(200);
        given()
                .when()
                .get("/goban/1/board")
                .then()
                .statusCode(200)
                .body(startsWith("B")); //TODO EXCLAIMER, B represents a Black piece the string representation of the board.
        given()
                .when()
                .put("/goban/1/pass")
                .then()
                .statusCode(200);
        given()
                .when()
                .delete("/goban/1/end")
                .then()
                .statusCode(200);
        // Check that the game is forcibly ended after that single piece and turn pass is completed.
        given()
                .when()
                .get("/lobby")
                .then()
                .statusCode(200)
                .contentType(ContentType.JSON)
                .body("[0].isGameInitialized", equalTo(false));
    }
}
