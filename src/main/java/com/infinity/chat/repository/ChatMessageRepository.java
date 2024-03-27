package com.infinity.chat.repository;

import com.infinity.chat.models.ChatMessage;

import org.springframework.data.mongodb.repository.MongoRepository;

import java.util.List;

public interface ChatMessageRepository extends MongoRepository<ChatMessage, String > {
    List<ChatMessage> findByChatId(String chatId);
}
