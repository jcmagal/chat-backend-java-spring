package com.infinity.chat.repository;

import com.infinity.chat.models.Status;
import com.infinity.chat.models.User;
import org.springframework.data.mongodb.repository.MongoRepository;

import java.util.List;

public interface UserRepository extends MongoRepository<User, String> {
    List<User> findAllByStatus(Status status);

}
