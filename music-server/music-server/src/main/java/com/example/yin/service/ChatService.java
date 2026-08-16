package com.example.yin.service;

import com.example.yin.domain.ChatMessage;
import java.util.List;

public interface ChatService {
    String chatWithAI(Integer userId, String userMessage);
    List<ChatMessage> getChatHistory(Integer userId);
}
