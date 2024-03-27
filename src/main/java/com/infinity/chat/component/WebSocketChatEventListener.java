package com.infinity.chat.component;


import com.infinity.chat.models.Status;
import com.infinity.chat.models.User;
import com.infinity.chat.models.UserStatusEvent;
import com.infinity.chat.repository.UserRepository;
import com.infinity.chat.services.UserActivityTracker;
import com.infinity.chat.services.UserService;
import lombok.RequiredArgsConstructor;
import org.springframework.context.event.EventListener;
import org.springframework.messaging.simp.SimpMessageSendingOperations;
import org.springframework.messaging.simp.stomp.StompHeaderAccessor;
import org.springframework.stereotype.Component;
import org.springframework.web.socket.messaging.SessionConnectedEvent;
import org.springframework.web.socket.messaging.SessionDisconnectEvent;

import java.util.Optional;

@Component
@RequiredArgsConstructor
public class WebSocketChatEventListener {
    private final SimpMessageSendingOperations messagingTemplate;

    @EventListener
    public void handleWebSocketConnectListener(SessionConnectedEvent event) {
        System.out.println("Received a new web socket connection");
    }

    @EventListener
    public void handleWebSocketDisconnectListener(SessionDisconnectEvent event) {
        StompHeaderAccessor headerAccessor = StompHeaderAccessor.wrap(event.getMessage());
        String nickname = (String) headerAccessor.getSessionAttributes().get("nickname");
        if (nickname != null) {
            UserStatusEvent userStatusEvent = new UserStatusEvent(nickname, "FullName", "OFFLINE");
            messagingTemplate.convertAndSend("/topic/public", userStatusEvent);
        }
    }

}
