package com.example.yin.domain;

public class ChatMessage {
    private Integer id;
    private Integer userId;
    private String content;
    private String role;
    private String aiResponse;
    private String createTime;

    public Integer getId() {
        return id;
    }

    public void setId(Integer id) {
        this.id = id;
    }

    public Integer getUserId() {
        return userId;
    }

    public void setUserId(Integer userId) {
        this.userId = userId;
    }

    public String getContent() {
        return content;
    }

    public void setContent(String content) {
        this.content = content;
    }

    public String getRole() {
        return role;
    }

    public void setRole(String role) {
        this.role = role;
    }

    public String getAiResponse() {
        return aiResponse;
    }

    public void setAiResponse(String aiResponse) {
        this.aiResponse = aiResponse;
    }

    public String getCreateTime() {
        return createTime;
    }

    public void setCreateTime(String createTime) {
        this.createTime = createTime;
    }

    public static Builder builder() {
        return new Builder();
    }

    public static class Builder {
        private ChatMessage message = new ChatMessage();

        public Builder role(String role) {
            message.setRole(role);
            return this;
        }

        public Builder content(String content) {
            message.setContent(content);
            return this;
        }

        public ChatMessage build() {
            return message;
        }
    }
}
