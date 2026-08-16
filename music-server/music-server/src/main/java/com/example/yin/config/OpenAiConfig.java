package com.example.yin.config;

import okhttp3.Call;
import okhttp3.EventListener;
import okhttp3.OkHttpClient;
import okhttp3.Request;
import okhttp3.Response;
import okhttp3.MediaType;
import okhttp3.RequestBody;
import okhttp3.EventListener.Factory;
import okhttp3.ResponseBody;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

import java.io.IOException;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.concurrent.TimeUnit;

@Configuration
public class OpenAiConfig {

    @Bean
    public OpenAiClient openAiClient(
        @Value("${ai.openai.api-key}") String apiKey,
        @Value("${ai.openai.base-url}") String baseUrl,
        @Value("${ai.openai.model}") String model
    ) {
        return new OpenAiClient(apiKey, baseUrl, model);
    }

    public static class OpenAiClient {
        private final OkHttpClient httpClient;
        private final ObjectMapper objectMapper;
        
        private final String apiKey;
        private final String baseUrl;
        private final String model;

        public OpenAiClient(String apiKey, String baseUrl, String model) {
            this.apiKey = apiKey;
            this.baseUrl = baseUrl;
            this.model = model;
            
            this.httpClient = new OkHttpClient.Builder()
                .connectTimeout(30, TimeUnit.SECONDS)
                .readTimeout(60, TimeUnit.SECONDS)
                .writeTimeout(60, TimeUnit.SECONDS)
                .build();
            this.objectMapper = new ObjectMapper();
        }

        public String chat(String userMessage) throws IOException {
            Map<String, Object> requestBody = new HashMap<>();
            requestBody.put("model", model);
            
            Map<String, String> systemMessage = new HashMap<>();
            systemMessage.put("role", "system");
            systemMessage.put("content", "你是一个专业的音乐推荐助手，专门帮助用户查找和推荐音乐。你可以根据用户的情绪、偏好和场景推荐合适的歌曲。请用中文回复。");
            
            Map<String, String> userMsg = new HashMap<>();
            userMsg.put("role", "user");
            userMsg.put("content", userMessage);
            
            requestBody.put("messages", new Object[]{systemMessage, userMsg});
            requestBody.put("stream", false);

            String jsonBody = objectMapper.writeValueAsString(requestBody);

            Request request = new Request.Builder()
                .url(baseUrl + "/v1/chat/completions")
                .addHeader("Authorization", "Bearer " + apiKey)
                .addHeader("Content-Type", "application/json")
                .post(RequestBody.create(jsonBody, MediaType.get("application/json")))
                .build();

            try (Response response = httpClient.newCall(request).execute()) {
                if (response.isSuccessful() && response.body() != null) {
                    String responseBody = response.body().string();
                    Map<String, Object> responseMap = objectMapper.readValue(responseBody, Map.class);
                    
                    List<Map<String, Object>> choices = (List<Map<String, Object>>) responseMap.get("choices");
                    if (choices != null && !choices.isEmpty()) {
                        Map<String, Object> firstChoice = choices.get(0);
                        Map<String, String> message = (Map<String, String>) firstChoice.get("message");
                        if (message != null) {
                            return message.get("content");
                        }
                    }
                    
                    return "抱歉，我暂时无法回复您的问题: API 返回格式异常";
                } else {
                    return "抱歉，我暂时无法回复您的问题: " + response.code();
                }
            }
        }
    }
}
