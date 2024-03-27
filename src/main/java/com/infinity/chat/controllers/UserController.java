package com.infinity.chat.controllers;

import com.infinity.chat.services.UserActivityTracker;
import com.infinity.chat.models.User;
import com.infinity.chat.services.UserService;
import lombok.RequiredArgsConstructor;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.http.ResponseEntity;
import org.springframework.messaging.handler.annotation.Header;
import org.springframework.messaging.handler.annotation.MessageMapping;
import org.springframework.messaging.handler.annotation.Payload;
import org.springframework.messaging.handler.annotation.SendTo;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.GetMapping;

import java.util.List;

@Controller
@RequiredArgsConstructor
public class UserController {
    private final UserService userService;
    private final UserActivityTracker userActivityTracker;



    private static final Logger log = LoggerFactory.getLogger(UserController.class);

    @MessageMapping("/user.addUser")
    @SendTo("/topic/userUpdates")
    public User addUser(@Payload User user,
                              @Header("simpSessionId") String sessionId) {
        userService.saveUser(user);
        userActivityTracker.registerUserSession(sessionId, user.getNickName());
        log.info("Usuário {} entrou", user.getNickName());
        return user;
    }

    @MessageMapping("/user.disconnectUser")
    public User disconnectUser(
            @Payload User user){
        userService.disconnect(user);
        return user;
    }

    @GetMapping("/users")
    public ResponseEntity<List<User>> findConnectedUsers() {
        return ResponseEntity.ok(userService.findAllUsers());
    }
}
