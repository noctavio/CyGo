package com.example.sb.Controller;

import java.io.IOException;
import java.util.Hashtable;
import java.util.List;
import java.util.Map;
import java.util.Optional;

import com.example.sb.Entity.Player;
import com.example.sb.Entity.TheProfile;
import com.example.sb.Entity.User;
import com.example.sb.Repository.MessageRepository;
import com.example.sb.Entity.Message;
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
@ServerEndpoint(value = "/gamechat/{username}")  // this is Websocket url
public class ChatController {

    // cannot autowire static directly (instead we do it by the below
    // method
    private static MessageRepository msgRepo;

    @Autowired
    private UserRepository userRepository;
    @Autowired
    private TheProfileRepository profileRepository;
    @Autowired
    private PlayerRepository playerRepository;

    /*
     * Grabs the MessageRepository singleton from the Spring Application
     * Context.  This works because of the @Controller annotation on this
     * class and because the variable is declared as static.
     * There are other ways to set this. However, this approach is
     * easiest.
     */
    @Autowired
    public void setMessageRepository(MessageRepository repo) {
        msgRepo = repo;  // we are setting the static variable
    }

    // Store all socket session and their corresponding username.
    private static Map<Session, String> sessionUsernameMap = new Hashtable<>();
    private static Map<String, Session> usernameSessionMap = new Hashtable<>();

    private final Logger logger = LoggerFactory.getLogger(ChatController.class);

    @OnOpen
    public void onOpen(Session session, @PathParam("username") String username)
            throws IOException {

        logger.info("Entered into Open");

        // store connecting user information
        sessionUsernameMap.put(session, username);
        usernameSessionMap.put(username, session);

        //Send chat history to the newly connected user
        sendMsgToUser(username, getChatHistory());

        // broadcast that new user joined
        String message = username + " has joined";
        broadcast(message);
    }

    @OnMessage
    public void onMessage(Session session, String message) throws IOException {
        // Handle new messages
        logger.info("Entered into Message: Got Message:" + message);
        String senderName = sessionUsernameMap.get(session);

        // Direct message to a user using the format "/whipser username <message>"
        if (message.startsWith("/whisper")) {
            String[] parts = message.split(" ", 3); // Split into 3 parts: ["/whisper", "username", "message"]

            String recipientName = parts[1]; // Get the recipient's name
            String whisperMessage = parts.length > 2 ? parts[2] : ""; // Get the rest of the message or empty string if no message

            // send message to target, and display for sender
            sendMsgToUser(recipientName, "From @" + senderName + ": " + whisperMessage);
            sendMsgToUser(senderName, "To @" +  recipientName + ": " +  whisperMessage);

        }
        else { // broadcast
            broadcast(senderName + ": " + message);
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
        String message = username + " disconnected";
        broadcast(message);
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

    private void broadcast(String message) {
        sessionUsernameMap.forEach((session, recipient) -> {
            try {
                Player currentRecipient = playerRepository.findByUsername(recipient);

                int colonIndex = message.indexOf(':');

                // Extract the name before the hard code colon.
                String senderName = message.substring(0, colonIndex).trim();

                if (currentRecipient.getMuted().contains(senderName)) {
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
                sb.append(message.getUserName() + ": " + message.getContent() + "\n");
            }
        }
        return sb.toString();
    }
}
