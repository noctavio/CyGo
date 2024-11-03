package com.example.sb.Controller;

import java.io.IOException;
import java.time.Instant;
import java.util.Hashtable;
import java.util.List;
import java.util.Map;
import java.util.Optional;
import java.util.concurrent.ConcurrentHashMap;

import com.example.sb.Model.Player;
import com.example.sb.Model.TheProfile;
import com.example.sb.Model.User;
import com.example.sb.Repository.MessageRepository;
import com.example.sb.Model.Message;
import com.example.sb.Repository.PlayerRepository;
import com.example.sb.Repository.TheProfileRepository;
import com.example.sb.Repository.UserRepository;
import jakarta.websocket.OnClose;
import jakarta.websocket.OnError;
import jakarta.websocket.OnMessage;
import jakarta.websocket.OnOpen;
import jakarta.websocket.Session;
import jakarta.websocket.server.PathParam;
import jakarta.websocket.server.ServerEndpoint;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;

@Controller      // this is needed for this to be an endpoint to springboot
@ServerEndpoint(value = "/{username}/join/gamechat")  // this is Websocket url
public class ChatController {

    // cannot autowire static, must be done in static method otherwise you can't access the Repo's at all
    // This is because @ServerEndpoint is not part of springboot so it won't auto-detect beans.
    private static MessageRepository msgRepo;

    private static UserRepository userRepository;

    private static TheProfileRepository profileRepository;

    private static PlayerRepository playerRepository;

    // Store all socket session and their corresponding username.
    private static Map<Session, String> sessionUsernameMap = new Hashtable<>();

    private static Map<String, Session> usernameSessionMap = new Hashtable<>();

    private final Logger logger = LoggerFactory.getLogger(ChatController.class);

    private static final long COOLDOWN_PERIOD_MS = 1000; // 1 second in milliseconds

    // Map to store player ID and their last message timestamp
    private static ConcurrentHashMap<String, Instant> playerLastMessageTime = new ConcurrentHashMap<>();

    /*
     * Grabs the MessageRepository singleton from the Spring Application
     * Context.  This works because of the @Controller annotation on this
     * class and because the variable is declared as static.
     * There are other ways to set this. However, this approach is
     * easiest.
     */
    @Autowired
    public void setMessageRepository(MessageRepository repo, UserRepository userRepo, TheProfileRepository profileRepo, PlayerRepository playerRepo) {
        // we are setting the static variable
        msgRepo = repo;
        userRepository = userRepo;
        profileRepository = profileRepo;
        playerRepository = playerRepo;
    }

    @OnOpen
    public void onOpen(Session session, @PathParam("username") String username) throws IOException {

        logger.info("Entered into Open");

        // store connecting user information
        sessionUsernameMap.put(session, username);
        usernameSessionMap.put(username, session);

        //Send chat history to the newly connected user
        sendMsgToUser(username, getChatHistory());

        // broadcast that new user joined
        String message = username + " - [JOINED]";
        broadcast(message, true);
    }

    @OnMessage
    public void onMessage(Session session, String message) throws IOException {
        // Handle new messages
        logger.info("Entered into Message: Got Message:" + message);
        String senderName = sessionUsernameMap.get(session);
        Instant nowTime = Instant.now();

        Instant lastMessageTime = playerLastMessageTime.getOrDefault(senderName, Instant.EPOCH);

        // Direct message to a user using the format "/whipser username <message>"
        if (nowTime.isAfter(lastMessageTime.plusMillis(COOLDOWN_PERIOD_MS))) {
            playerLastMessageTime.put(senderName, nowTime);

            if (message.startsWith("/whisper")) {
                String[] parts = message.split(" ", 3); // Split into 3 parts ["/whisper", "username", "message"]

                String recipientName = parts[1]; // Get the recipient's name
                String whisperMessage = parts.length > 2 ? parts[2] : ""; // Get the rest of the message or empty string if no message

                User user = userRepository.findByUsername(recipientName);
                if (user == null) {
                    throw new RuntimeException("User not found");
                }

                TheProfile profile = profileRepository.findByUser(Optional.of(user));
                if (profile == null) {
                    throw new RuntimeException("Profile not found for specified user ");
                }
                Player currentRecipient = playerRepository.findByProfile(profile);

                if (currentRecipient.getMuted().contains(senderName)) {
                    sendMsgToUser(senderName,"[ALERT] Message not sent, you were muted by " + recipientName);
                }
                else {
                    sendMsgToUser(recipientName, "[FROM] @" + senderName + " - " + whisperMessage);
                    sendMsgToUser(senderName, "[TO] @" +  recipientName + " - " +  whisperMessage);
                }
            }
            else { // broadcast
                broadcast(senderName + " - " + message, false);
            }
        }
        else {
            sendMsgToUser(senderName,"[ALERT] Please wait before sending another message");
            return;
        }

        // Saving chat history to repository
        msgRepo.save(new Message(senderName, message));
    }

    @OnClose
    public void onClose(Session session) throws IOException {
        logger.info("Entered into Close");

        // remove the user connection information
        String username = sessionUsernameMap.get(session);
        sessionUsernameMap.remove(session);
        usernameSessionMap.remove(username);

        // broadcast that the user disconnected
        String message = username + " - [DISCONNECTED]";
        broadcast(message, true);
    }

    @OnError
    public void onError(Session session, Throwable throwable) {
        // Do error handling here
        logger.info("Entered into Error");
        throwable.printStackTrace();
    }

    private void sendMsgToUser(String username, String message) {
        try {
            usernameSessionMap.get(username).getBasicRemote().sendText(message);
        }
        catch (IOException e) {
            logger.info("Exception: " + e.getMessage().toString());
            e.printStackTrace();
        }
    }

    private void broadcast(String message, Boolean forceSend) {
        sessionUsernameMap.forEach((session, recipient) -> {
            try {
                User user = userRepository.findByUsername(recipient);
                if (user == null) {
                    throw new RuntimeException("User not found");
                }

                // Retrieve the Profile associated with the User
                TheProfile profile = profileRepository.findByUser(Optional.of(user));
                if (profile == null) {
                    throw new RuntimeException("Profile not found for specified user ");
                }
                Player currentRecipient = playerRepository.findByProfile(profile);

                int colonIndex = message.indexOf('-');

                String senderName = message.substring(0, colonIndex).trim(); // Extract the name before the hard code colon.

                if (currentRecipient.getMuted().contains(senderName) && !forceSend) {
                    return;
                }

                session.getBasicRemote().sendText(message);
            }
            catch (IOException e) {
                logger.info("Exception: " + e.getMessage().toString());
                e.printStackTrace();
            }
        });
    }

    // Gets the Chat history from the repository
    private String getChatHistory() {
        List<Message> messages = msgRepo.findAll();

        // convert the list to a string
        StringBuilder sb = new StringBuilder();
        if (messages != null && messages.size() != 0) {
            for (Message message : messages) {
                sb.append(message.getUserName() + " - " + message.getContent() + "\n");
            }
        }
        return sb.toString();
    }
}
