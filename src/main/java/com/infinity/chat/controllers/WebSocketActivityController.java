package com.infinity.chat.controllers;


import com.infinity.chat.component.WebSocketChatEventListener;
import com.infinity.chat.models.UserStatusEvent;
import com.infinity.chat.repository.UserRepository;
import com.infinity.chat.services.UserActivityTracker;
import com.infinity.chat.services.UserService;
import lombok.RequiredArgsConstructor;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.context.event.EventListener;
import org.springframework.messaging.handler.annotation.Header;
import org.springframework.messaging.handler.annotation.MessageMapping;
import org.springframework.messaging.simp.SimpMessagingTemplate;
import org.springframework.stereotype.Controller;
import org.springframework.web.socket.messaging.SessionDisconnectEvent;


@Controller
@RequiredArgsConstructor
public class WebSocketActivityController {
    private final UserActivityTracker userActivityTracker;
    private final UserService userService;
    private final UserRepository repository;

    private final SimpMessagingTemplate messagingTemplate;

    private static final Logger log = LoggerFactory.getLogger(WebSocketActivityController.class);

    @MessageMapping("/heartbeat")
    public void handleHeartbeat(@Header("simpSessionId") String sessionId) {
        userActivityTracker.updateUserActivity(sessionId);
    }

    @EventListener
    public void handleSessionDisconnect(SessionDisconnectEvent event) {
        String sessionId = event.getSessionId();
        String nickName = userActivityTracker.getNickNameBySessionId(sessionId);
        if (nickName != null) {
            repository.findById(nickName).ifPresent(user -> {
                String fullName = user.getFullName();
                messagingTemplate.convertAndSend("/topic/disconnectEvent", new UserStatusEvent(nickName,  fullName, "OFFLINE"));
                userActivityTracker.removeUserSession(sessionId);
                log.info("Usuário {} saiu");
            });
        }
    }

}
