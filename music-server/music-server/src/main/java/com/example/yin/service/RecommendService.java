package com.example.yin.service;

import com.example.yin.domain.Song;
import com.example.yin.domain.UserItem;

import java.util.List;

public interface RecommendService {
    
    List<Song> getCollaborativeFilteringRecommendations(Integer userId, int limit);
    
    List<Song> getContentBasedRecommendations(Integer userId, int limit);
    
    List<Song> getPopularSongs(int limit);
    
    List<UserItem> getUserItemMatrix();
}
