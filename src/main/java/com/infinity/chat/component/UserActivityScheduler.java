package com.infinity.chat.component;

import com.infinity.chat.repository.UserRepository;
import com.infinity.chat.services.UserActivityTracker;
import com.infinity.chat.services.UserService;
import lombok.RequiredArgsConstructor;
import org.springframework.scheduling.annotation.EnableScheduling;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Component;


@Component
@EnableScheduling
@RequiredArgsConstructor
public class UserActivityScheduler {
    private final UserActivityTracker userActivityTracker;
    private final UserService userService;
    private final UserRepository repository;

    @Scheduled(fixedRate = 60000)
    public void checkInactiveUsers() {
        userActivityTracker.checkAndHandleInactiveUsers(userService, repository, 1);
    }
}

