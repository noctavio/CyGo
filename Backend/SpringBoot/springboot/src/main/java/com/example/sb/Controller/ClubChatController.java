package com.example.sb.Controller;

import com.example.sb.Model.Message;
import com.example.sb.Model.Player;
import com.example.sb.Model.TheProfile;
import com.example.sb.Model.User;
import com.example.sb.Repository.MessageRepository;
import com.example.sb.Repository.PlayerRepository;
import com.example.sb.Repository.TheProfileRepository;
import com.example.sb.Repository.UserRepository;
import jakarta.websocket.*;
import jakarta.websocket.server.PathParam;
import jakarta.websocket.server.ServerEndpoint;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;

import java.io.IOException;
import java.time.Instant;
import java.util.Hashtable;
import java.util.List;
import java.util.Map;
import java.util.Optional;
import java.util.concurrent.ConcurrentHashMap;

@Controller      // this is needed for this to be an endpoint to springboot
@ServerEndpoint("/clubChat/{clubName}/{username}")
public class ClubChatController {

    // Store all socket sessions and their corresponding club names and usernames
    private static Map<String, Map<Session, String>> clubSessionsMap = new Hashtable<>();
    private static Map<Session, String> sessionClubNameMap = new Hashtable<>();
    private static Map<Session, String> sessionUsernameMap = new Hashtable<>();  // New map for usernames

    // server side logger
    private final Logger logger = LoggerFactory.getLogger(ClubChatController.class);

    /**
     * This method is called when a new WebSocket connection is established.
     *
     * @param session  represents the WebSocket session for the connected club.
     * @param clubName specified in path parameter.
     * @param username specified in path parameter.
     */
    @OnOpen
    public void onOpen(Session session, @PathParam("clubName") String clubName, @PathParam("username") String username) throws IOException {
        // Log the club name and username for debugging
        logger.info("[onOpen] Club: " + clubName + ", Username: " + username);

        // Check if there is an active session map for this club name
        clubSessionsMap.putIfAbsent(clubName, new Hashtable<>());

        // Associate the session with the club and username
        clubSessionsMap.get(clubName).put(session, username);  // Store username instead of club name
        sessionClubNameMap.put(session, clubName);
        sessionUsernameMap.put(session, username);  // Store the username

        // Send a welcome message only to the joining user
        sendMessageToParticularUser(session, "Welcome to the " + clubName + " chat, " + username + "!");

        // Broadcast a message only to users of the same club
        broadcast(clubName, clubName + ": " + username + " has joined the chat");
    }

    /**
     * Handles incoming WebSocket messages from a client.
     *
     * @param session The WebSocket session representing the client's connection.
     * @param message The message received from the client.
     */
    @OnMessage
    public void onMessage(Session session, String message) throws IOException {
        // Get the club name and username by session
        String clubName = sessionClubNameMap.get(session);
        String username = sessionUsernameMap.get(session);  // Retrieve the correct username
        if (clubName == null || username == null) return;

        // Log the incoming message
        logger.info("[onMessage] " + username + ": " + message);

        // Direct message to a specific club member using the format "@memberName <message>"
        if (message.startsWith("@")) {
            String[] split_msg = message.split("\\s+", 2);
            String destClubMember = split_msg[0].substring(1); // Remove '@' from the name
            String actualMessage = split_msg.length > 1 ? split_msg[1] : "";

            // Get the session of the destination club member
            Session destSession = clubSessionsMap.get(clubName).keySet().stream()
                    .filter(s -> sessionUsernameMap.get(s).equals(destClubMember))
                    .findFirst()
                    .orElse(null);

            // Send the message only if the destination session exists
            if (destSession != null) {
                sendMessageToParticularUser(destSession, "[DM from " + username + "]: " + actualMessage);
            } else {
                sendMessageToParticularUser(session, "User " + destClubMember + " not found in the club.");
            }
        } else {
            // Broadcast the message to everyone in the same club, including the sender's username
            broadcast(clubName, username + ": " + message);
        }
    }

    /**
     * Handles the closure of a WebSocket connection.
     *
     * @param session The WebSocket session that is being closed.
     */
    @OnClose
    public void onClose(Session session) throws IOException {
        String clubName = sessionClubNameMap.get(session);
        String username = sessionUsernameMap.get(session);
        if (clubName == null || username == null) return;

        logger.info("[onClose] " + clubName + ", Username: " + username);

        // Remove the session from the mappings
        clubSessionsMap.get(clubName).remove(session);
        sessionClubNameMap.remove(session);
        sessionUsernameMap.remove(session);

        // Notify the remaining club members of the disconnection
        broadcast(clubName, clubName + ": " + username + " has disconnected");
    }

    /**
     * Handles WebSocket errors that occur during the connection.
     *
     * @param session   The WebSocket session where the error occurred.
     * @param throwable The Throwable representing the error condition.
     */
    @OnError
    public void onError(Session session, Throwable throwable) {
        String clubName = sessionClubNameMap.get(session);
        String username = sessionUsernameMap.get(session);
        if (clubName == null || username == null) return;

        logger.info("[onError] " + clubName + ", Username: " + username + ": " + throwable.getMessage());
    }

    /**
     * Sends a message to a specific user in the chat (DM).
     *
     * @param session The WebSocket session of the recipient.
     * @param message The message to be sent.
     */
    private void sendMessageToParticularUser(Session session, String message) {
        if (session != null) {
            try {
                session.getBasicRemote().sendText(message);
            } catch (IOException e) {
                logger.info("[DM Exception] " + e.getMessage());
            }
        }
    }

    /**
     * Broadcasts a message to all users in the same club.
     *
     * @param clubName The name of the club to which the message is broadcasted.
     * @param message  The message to be broadcasted to all club members.
     */
    private void broadcast(String clubName, String message) {
        Map<Session, String> clubSessions = clubSessionsMap.get(clubName);
        if (clubSessions != null) {
            clubSessions.forEach((session, name) -> {
                try {
                    session.getBasicRemote().sendText(message);
                } catch (IOException e) {
                    logger.info("[Broadcast Exception] " + e.getMessage());
                }
            });
        }
    }
}