package com.example.sb.Controller;

import com.example.sb.Model.Club;
import com.example.sb.Model.ClubMessage;
import com.example.sb.Repository.*;
import jakarta.websocket.*;
import jakarta.websocket.server.PathParam;
import jakarta.websocket.server.ServerEndpoint;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;

import java.io.IOException;
import java.util.Hashtable;
import java.util.Map;

@Controller
@ServerEndpoint("/clubChat/{clubName}/{username}")
public class ClubChatController {

    private static Map<String, Map<Session, String>> clubSessionsMap = new Hashtable<>();
    private static Map<Session, String> sessionClubNameMap = new Hashtable<>();
    private static Map<Session, String> sessionUsernameMap = new Hashtable<>();

    private final Logger logger = LoggerFactory.getLogger(ClubChatController.class);

    private static ClubMessageRepository msgRepo;

    @Autowired
    public void setMessageRepository(ClubMessageRepository repo) {
        // we are setting the static variable
        msgRepo = repo;
    }

    // Manual dependency injection workaround during WebSocket initialization
    @OnOpen
    public void onOpen(Session session, @PathParam("clubName") String clubName, @PathParam("username") String username) throws IOException {
        logger.info("[onOpen] Club: " + clubName + ", Username: " + username);

        // Initialize Spring Beans manually

        clubSessionsMap.putIfAbsent(clubName, new Hashtable<>());
        clubSessionsMap.get(clubName).put(session, username);
        sessionClubNameMap.put(session, clubName);
        sessionUsernameMap.put(session, username);

        sendMessageToParticularUser(session, "Welcome to the " + clubName + " chat, " + username + "!");
        broadcast(clubName, clubName + ": " + username + " has joined the chat");
    }

    @OnMessage
    public void onMessage(Session session, String message) throws IOException {
        String clubName = sessionClubNameMap.get(session);
        String username = sessionUsernameMap.get(session);

        if (clubName == null || username == null) return;

        logger.info("[onMessage] " + username + ": " + message);

        if (message.startsWith("@")) {
            String[] split_msg = message.split("\\s+", 2);
            String destClubMember = split_msg[0].substring(1);
            String actualMessage = split_msg.length > 1 ? split_msg[1] : "";

            Session destSession = clubSessionsMap.get(clubName).keySet().stream()
                    .filter(s -> sessionUsernameMap.get(s).equals(destClubMember))
                    .findFirst()
                    .orElse(null);

            if (destSession != null) {
                sendMessageToParticularUser(destSession, "[DM from " + username + "]: " + actualMessage);
            } else {
                sendMessageToParticularUser(session, "User " + destClubMember + " not found in the club.");
            }
        } else {
            broadcast(clubName, username + ": " + message);
        }
        msgRepo.save(new ClubMessage(clubName, username, message));
    }

    @OnClose
    public void onClose(Session session) throws IOException {
        String clubName = sessionClubNameMap.get(session);
        String username = sessionUsernameMap.get(session);
        if (clubName == null || username == null) return;

        logger.info("[onClose] " + clubName + ", Username: " + username);

        clubSessionsMap.get(clubName).remove(session);
        sessionClubNameMap.remove(session);
        sessionUsernameMap.remove(session);

        broadcast(clubName, clubName + ": " + username + " has disconnected");
    }

    @OnError
    public void onError(Session session, Throwable throwable) {
        String clubName = sessionClubNameMap.get(session);
        String username = sessionUsernameMap.get(session);
        if (clubName == null || username == null) return;

        logger.error("[onError] " + clubName + ", Username: " + username + ": " + throwable.getMessage());
    }

    private void sendMessageToParticularUser(Session session, String message) {
        if (session != null) {
            try {
                session.getBasicRemote().sendText(message);
            } catch (IOException e) {
                logger.error("[DM Exception] " + e.getMessage());
            }
        }
    }

    private void broadcast(String clubName, String message) {
        Map<Session, String> clubSessions = clubSessionsMap.get(clubName);
        if (clubSessions != null) {
            clubSessions.forEach((session, name) -> {
                try {
                    session.getBasicRemote().sendText(message);
                } catch (IOException e) {
                    logger.error("[Broadcast Exception] " + e.getMessage());
                }
            });
        }
    }
}