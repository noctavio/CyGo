package com.example.demo.websocket;

import org.springframework.web.socket.CloseStatus;
import org.springframework.web.socket.WebSocketHandler;
import org.springframework.web.socket.WebSocketMessage;
import org.springframework.web.socket.WebSocketSession;
import org.springframework.web.socket.handler.TextWebSocketHandler;
import org.springframework.stereotype.Component;

@Component
public class ChatServer extends TextWebSocketHandler {

    @Override
    public void handleTextMessage(WebSocketSession session, WebSocketMessage<?> message) throws Exception {
        // Handle incoming messages here, broadcast to other clients, etc.
        session.sendMessage(message); // Echoing back for simplicity
    }

    @Override
    public void afterConnectionEstablished(WebSocketSession session) throws Exception {
        // Handle actions on connection established
    }

    @Override
    public void afterConnectionClosed(WebSocketSession session, CloseStatus status) throws Exception {
        // Handle actions on connection close
    }
}
