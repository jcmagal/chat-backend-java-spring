package com.infinity.chat.services;


import com.infinity.chat.models.Status;
import com.infinity.chat.models.User;
import com.infinity.chat.repository.UserRepository;
import jakarta.annotation.PostConstruct;
import lombok.RequiredArgsConstructor;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.messaging.simp.SimpMessagingTemplate;
import org.springframework.stereotype.Service;

import java.util.List;
@Service
@RequiredArgsConstructor
public class UserService {
    private final UserRepository userRepository;
    private static final Logger log = LoggerFactory.getLogger(UserService.class);

    private final SimpMessagingTemplate messagingTemplate;

    public void saveUser(User user){
        user.setStatus(Status.ONLINE);
        userRepository.save(user);
        log.info("Usuário {} conectado", user.getNickName());
    }

    public void disconnect(User user){
        var storedUser = userRepository.findById(user.getNickName()).orElse(null);
        if (storedUser != null){
            storedUser.setStatus(Status.OFFLINE);
            userRepository.save(storedUser);
            log.info("Usuário {} offline", user.getNickName());
        }
    }

    @PostConstruct
    public void resetUserStatuses() {
        List<User> onlineUsers = userRepository.findAllByStatus(Status.ONLINE);
        onlineUsers.forEach(user -> {
            user.setStatus(Status.OFFLINE);
            userRepository.save(user);
            log.info("Usuário OFFLINE", user.getNickName());
        });
    }

    public List<User> findAllUsers() {
        return userRepository.findAll();
    }
    public List<User> findConnectedUsers(){
        return userRepository.findAllByStatus(Status.ONLINE);
    }
}
