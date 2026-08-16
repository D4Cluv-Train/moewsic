package com.example.yin.service.impl;

import com.example.yin.config.OpenAiConfig;
import com.example.yin.domain.ChatMessage;
import com.example.yin.service.ChatService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.ArrayList;
import java.util.List;

@Service
public class ChatServiceImpl implements ChatService {

    @Autowired
    private OpenAiConfig.OpenAiClient openAiClient;

    @Override
    public String chatWithAI(Integer userId, String userMessage) {
        try {
            String aiResponse = openAiClient.chat(userMessage);
            saveChatMessage(userId, userMessage, aiResponse);
            return aiResponse;
        } catch (Exception e) {
            return "抱歉，我暂时无法回复您的问题: " + e.getMessage();
        }
    }

    @Override
    public List<ChatMessage> getChatHistory(Integer userId) {
        return new ArrayList<>();
    }

    private void saveChatMessage(Integer userId, String userMessage, String aiResponse) {
        ChatMessage userMsg = new ChatMessage();
        userMsg.setUserId(userId);
        userMsg.setContent(userMessage);
        userMsg.setRole("user");
        userMsg.setAiResponse(aiResponse);
        
        System.out.println("保存聊天记录: userId=" + userId + ", message=" + userMessage);
    }
}
