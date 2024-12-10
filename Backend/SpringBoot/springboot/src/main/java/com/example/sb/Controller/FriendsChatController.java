package com.example.sb.Controller;

import com.example.sb.Model.FriendMessage;
import com.example.sb.Repository.FriendsMessageRepository;
import jakarta.websocket.*;
import jakarta.websocket.server.PathParam;
import jakarta.websocket.server.ServerEndpoint;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;

import java.io.IOException;
import java.util.Hashtable;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

@Controller
@ServerEndpoint("/dmChat/{user1}/{user2}")
public class FriendsChatController {

    private static Map<String, Session> activeUserSessions = new ConcurrentHashMap<>();
    private static Map<String, List<String>> offlineMessageQueues = new ConcurrentHashMap<>();

    private final Logger logger = LoggerFactory.getLogger(FriendsChatController.class);

    private static FriendsMessageRepository friendMessageRepo;

    @Autowired
    public void setFriendMessageRepository(FriendsMessageRepository repo) {
        // Setting the static variable
        friendMessageRepo = repo;
    }

    @OnOpen
    public void onOpen(Session session, @PathParam("user1") String user1, @PathParam("user2") String user2) throws IOException {
        logger.info("[onOpen] User1: " + user1 + ", User2: " + user2);

        // Add the current session to active users
        activeUserSessions.put(user1, session);


        // Check if there are any offline messages for the user
        if (offlineMessageQueues.containsKey(user1)) {
            List<String> queuedMessages = offlineMessageQueues.get(user1);
            for (String message : queuedMessages) {
                sendMessageToParticularUser(session, message);
            }
            offlineMessageQueues.remove(user1); // Clear the queue after delivering messages
        }
    }

    @OnMessage
    public void onMessage(Session session, String message, @PathParam("user1") String user1, @PathParam("user2") String user2) throws IOException {
        String sender = activeUserSessions.containsKey(user1) ? user1 : user2;
        String recipient = sender.equals(user1) ? user2 : user1;

        logger.info("[onMessage] From " + sender + " to " + recipient + ": " + message);

        Session recipientSession = activeUserSessions.get(recipient);

        if (recipientSession != null) {
            // If the recipient is online, send the message immediately
            sendMessageToParticularUser(recipientSession, "[DM from " + sender + "]: " + message);
        } else {
            // If the recipient is offline, queue the message
            offlineMessageQueues.putIfAbsent(recipient, new LinkedList<>());
            offlineMessageQueues.get(recipient).add("[DM from " + sender + "]: " + message);
        }

        // Save the message to the repository
        friendMessageRepo.save(new FriendMessage(sender, recipient, message));
    }

    @OnClose
    public void onClose(Session session, @PathParam("user1") String user1) {
        logger.info("[onClose] User: " + user1);

        // Remove the session from active users
        activeUserSessions.remove(user1);
    }

    @OnError
    public void onError(Session session, Throwable throwable) {
        logger.error("[onError] Session ID: " + session.getId() + ": " + throwable.getMessage());
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
}
