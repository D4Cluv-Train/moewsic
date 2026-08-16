package com.example.yin.controller;

import com.example.yin.domain.ChatMessage;
import com.example.yin.service.ChatService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

@RestController
@RequestMapping("/chat")
public class ChatController {

    @Autowired
    private ChatService chatService;

    @PostMapping("/message")
    public Object sendMessage(@RequestBody Map<String, Object> requestBody) {
        Integer userId = (Integer) requestBody.get("userId");
        String message = (String) requestBody.get("message");
        
        String aiResponse = chatService.chatWithAI(userId, message);
        Map<String, Object> result = new HashMap<>();
        result.put("code", 1);
        result.put("msg", "发送成功");
        result.put("aiMessage", aiResponse);
        return result;
    }

    @GetMapping("/history")
    public Object getChatHistory(@RequestParam Integer userId) {
        List<ChatMessage> history = chatService.getChatHistory(userId);
        Map<String, Object> result = new HashMap<>();
        result.put("code", 1);
        result.put("msg", "获取成功");
        result.put("data", history);
        return result;
    }
}
