package com.infinity.chat.services;

import com.infinity.chat.repository.UserRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import java.time.Duration;
import java.time.Instant;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

@Service
@RequiredArgsConstructor
public class UserActivityTracker {
    private final ConcurrentHashMap<String, Instant> userLastActivity = new ConcurrentHashMap<>();
    private final ConcurrentHashMap<String, String> sessionUserMapping = new ConcurrentHashMap<>();
    public void updateUserActivity(String sessionId) {
        String nickName = sessionUserMapping.get(sessionId);
        if (nickName != null) {
            userLastActivity.put(nickName, Instant.now());
        }
    }
    public String getNickNameBySessionId(String sessionId) {
        return sessionUserMapping.get(sessionId);
    }

    public void registerUserSession(String sessionId, String nickName) {
        sessionUserMapping.put(sessionId, nickName);
    }
    public void removeUserSession(String sessionId) {
        String nickName = sessionUserMapping.remove(sessionId);
        userLastActivity.remove(nickName);
    }
    public void checkAndHandleInactiveUsers(UserService userService, UserRepository repository, long inactivityThresholdMinutes) {
        Instant now = Instant.now();
        userLastActivity.forEach((nickName, lastActivity) -> {
            if (Duration.between(lastActivity, now).toMinutes() > inactivityThresholdMinutes) {
                repository.findById(nickName).ifPresent(user -> {
                    userService.disconnect(user);
                    removeUserSession(sessionUserMapping.entrySet().stream().filter(entry -> nickName.equals(
                            entry.getValue())).findFirst().map(Map.Entry::getKey).orElse(null));
                });
            }
        });
    }
}