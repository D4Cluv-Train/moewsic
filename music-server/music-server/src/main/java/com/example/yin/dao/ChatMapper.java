package com.example.yin.dao;

import com.example.yin.domain.ChatMessage;
import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Param;
import org.springframework.stereotype.Repository;

import java.util.List;

@Mapper
@Repository
public interface ChatMapper {

    int insertChatMessage(ChatMessage chatMessage);

    List<ChatMessage> selectChatHistoryByUserId(@Param("userId") Integer userId);

    ChatMessage selectByPrimaryKey(Integer id);

    int updateChatMessage(ChatMessage chatMessage);
}
