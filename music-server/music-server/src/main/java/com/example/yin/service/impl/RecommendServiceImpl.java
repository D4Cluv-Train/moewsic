package com.example.yin.service.impl;

import com.example.yin.dao.CollectMapper;
import com.example.yin.dao.SongMapper;
import com.example.yin.domain.Collect;
import com.example.yin.domain.Song;
import com.example.yin.domain.UserItem;
import com.example.yin.service.RecommendService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.*;
import java.util.stream.Collectors;

@Service
public class RecommendServiceImpl implements RecommendService {
    
    @Autowired
    private CollectMapper collectMapper;
    
    @Autowired
    private SongMapper songMapper;
    
    @Override
    public List<Song> getCollaborativeFilteringRecommendations(Integer userId, int limit) {
        System.out.println("=== 协同过滤推荐 ===");
        System.out.println("用户ID: " + userId);
        
        List<Collect> allCollections = collectMapper.allCollect();
        List<Song> allSongs = songMapper.allSong();
        
        System.out.println("总收藏数: " + allCollections.size());
        System.out.println("总歌曲数: " + allSongs.size());
        
        if (allCollections.isEmpty() || allSongs.isEmpty()) {
            System.out.println("没有收藏数据或歌曲数据，返回热门歌曲");
            return getPopularSongs(limit);
        }
        
        Map<Integer, Set<Integer>> userSongMap = new HashMap<>();
        Map<Integer, Set<Integer>> songUserMap = new HashMap<>();
        
        for (Collect collect : allCollections) {
            if (collect.getSongId() == null || collect.getUserId() == null) {
                continue;
            }
            userSongMap.computeIfAbsent(collect.getUserId(), k -> new HashSet<>()).add(collect.getSongId());
            songUserMap.computeIfAbsent(collect.getSongId(), k -> new HashSet<>()).add(collect.getUserId());
        }
        
        Set<Integer> userSongs = userSongMap.getOrDefault(userId, new HashSet<>());
        
        System.out.println("用户" + userId + "收藏的歌曲数: " + userSongs.size());
        
        if (userSongs.isEmpty()) {
            System.out.println("用户没有收藏记录，返回热门歌曲");
            return getPopularSongs(limit);
        }
        
        Map<Integer, Double> songScores = new HashMap<>();
        
        for (Integer songId : userSongs) {
            Set<Integer> usersWhoLikedSong = songUserMap.getOrDefault(songId, new HashSet<>());
            
            for (Integer otherUser : usersWhoLikedSong) {
                Set<Integer> otherUserSongs = userSongMap.getOrDefault(otherUser, new HashSet<>());
                
                for (Integer otherSongId : otherUserSongs) {
                    if (!userSongs.contains(otherSongId)) {
                        double currentScore = songScores.getOrDefault(otherSongId, 0.0);
                        songScores.put(otherSongId, currentScore + 1.0 / Math.sqrt(otherUserSongs.size()));
                    }
                }
            }
        }
        
        System.out.println("候选推荐歌曲数: " + songScores.size());
        
        List<Map.Entry<Integer, Double>> sortedSongs = songScores.entrySet()
            .stream()
            .sorted(Map.Entry.<Integer, Double>comparingByValue().reversed())
            .limit(limit)
            .collect(Collectors.toList());
        
        List<Song> recommendations = new ArrayList<>();
        for (Map.Entry<Integer, Double> entry : sortedSongs) {
            Song song = songMapper.selectByPrimaryKey(entry.getKey());
            if (song != null) {
                recommendations.add(song);
                System.out.println("推荐歌曲: " + song.getName() + " (分数: " + entry.getValue() + ")");
            }
        }
        
        System.out.println("最终推荐歌曲数: " + recommendations.size());
        
        return recommendations.isEmpty() ? getPopularSongs(limit) : recommendations;
    }
    
    @Override
    public List<Song> getContentBasedRecommendations(Integer userId, int limit) {
        System.out.println("=== 基于内容的推荐 ===");
        System.out.println("用户ID: " + userId);
        
        List<Collect> userCollections = collectMapper.collectionOfUser(userId);
        
        System.out.println("用户收藏数: " + userCollections.size());
        
        if (userCollections.isEmpty()) {
            System.out.println("用户没有收藏记录，返回热门歌曲");
            return getPopularSongs(limit);
        }
        
        Set<Integer> userLikedSongs = userCollections.stream()
            .map(Collect::getSongId)
            .collect(Collectors.toSet());
        
        List<Song> likedSongs = new ArrayList<>();
        for (Integer songId : userLikedSongs) {
            Song song = songMapper.selectByPrimaryKey(songId);
            if (song != null) {
                likedSongs.add(song);
            }
        }
        
        System.out.println("用户喜欢的歌曲数: " + likedSongs.size());
        
        if (likedSongs.isEmpty()) {
            System.out.println("没有找到喜欢的歌曲，返回热门歌曲");
            return getPopularSongs(limit);
        }
        
        Map<Integer, Integer> songIdToIndex = new HashMap<>();
        Map<Integer, List<Integer>> songFeatures = new HashMap<>();
        
        int index = 0;
        for (Song song : likedSongs) {
            songIdToIndex.put(song.getId(), index++);
            songFeatures.put(song.getId(), extractFeatures(song));
        }
        
        Map<Integer, Double> songScores = new HashMap<>();
        
        for (Collect collect : userCollections) {
            if (collect.getSongId() == null) continue;
            
            List<Integer> likedFeatures = songFeatures.getOrDefault(collect.getSongId(), new ArrayList<>());
            
            for (Map.Entry<Integer, List<Integer>> entry : songFeatures.entrySet()) {
                Integer songId = entry.getKey();
                List<Integer> songFeaturesList = entry.getValue();
                
                if (!userLikedSongs.contains(songId) && !songId.equals(collect.getSongId())) {
                    double similarity = calculateJaccardSimilarity(likedFeatures, songFeaturesList);
                    if (similarity > 0) {
                        double currentScore = songScores.getOrDefault(songId, 0.0);
                        songScores.put(songId, currentScore + similarity);
                    }
                }
            }
        }
        
        System.out.println("候选推荐歌曲数: " + songScores.size());
        
        List<Map.Entry<Integer, Double>> sortedSongs = songScores.entrySet()
            .stream()
            .sorted(Map.Entry.<Integer, Double>comparingByValue().reversed())
            .limit(limit)
            .collect(Collectors.toList());
        
        List<Song> recommendations = new ArrayList<>();
        for (Map.Entry<Integer, Double> entry : sortedSongs) {
            Song song = songMapper.selectByPrimaryKey(entry.getKey());
            if (song != null) {
                recommendations.add(song);
                System.out.println("推荐歌曲: " + song.getName() + " (相似度: " + entry.getValue() + ")");
            }
        }
        
        System.out.println("最终推荐歌曲数: " + recommendations.size());
        
        return recommendations.isEmpty() ? getPopularSongs(limit) : recommendations;
    }
    
    @Override
    public List<Song> getPopularSongs(int limit) {
        System.out.println("=== 热门歌曲推荐 ===");
        
        List<Collect> allCollections = collectMapper.allCollect();
        
        System.out.println("总收藏数: " + allCollections.size());
        
        Map<Integer, Long> songCount = allCollections.stream()
            .filter(c -> c.getSongId() != null)
            .collect(Collectors.groupingBy(Collect::getSongId, Collectors.counting()));
        
        System.out.println("有收藏的歌曲数: " + songCount.size());
        
        List<Map.Entry<Integer, Long>> sortedSongs = songCount.entrySet()
            .stream()
            .sorted(Map.Entry.<Integer, Long>comparingByValue().reversed())
            .limit(limit)
            .collect(Collectors.toList());
        
        List<Song> popularSongs = new ArrayList<>();
        for (Map.Entry<Integer, Long> entry : sortedSongs) {
            Song song = songMapper.selectByPrimaryKey(entry.getKey());
            if (song != null) {
                popularSongs.add(song);
                System.out.println("热门歌曲: " + song.getName() + " (收藏数: " + entry.getValue() + ")");
            }
        }
        
        System.out.println("最终热门歌曲数: " + popularSongs.size());
        
        return popularSongs;
    }
    
    @Override
    public List<UserItem> getUserItemMatrix() {
        List<Collect> allCollections = collectMapper.allCollect();
        
        Map<Integer, Set<Integer>> userSongMap = new HashMap<>();
        
        for (Collect collect : allCollections) {
            if (collect.getSongId() != null && collect.getUserId() != null) {
                userSongMap.computeIfAbsent(collect.getUserId(), k -> new HashSet<>()).add(collect.getSongId());
            }
        }
        
        List<UserItem> userItems = new ArrayList<>();
        int itemId = 0;
        
        for (Map.Entry<Integer, Set<Integer>> entry : userSongMap.entrySet()) {
            UserItem userItem = new UserItem();
            userItem.setUserId(entry.getKey());
            userItem.setItemIds(new ArrayList<>(entry.getValue()));
            userItem.setItemCount(entry.getValue().size());
            userItem.setItemId(itemId++);
            userItems.add(userItem);
        }
        
        return userItems;
    }
    
    private List<Integer> extractFeatures(Song song) {
        List<Integer> features = new ArrayList<>();
        
        if (song.getSingerId() != null) {
            features.add(song.getSingerId());
        }
        
        if (song.getName() != null) {
            for (char c : song.getName().toCharArray()) {
                features.add((int) c);
            }
        }
        
        return features;
    }
    
    private double calculateJaccardSimilarity(List<Integer> list1, List<Integer> list2) {
        if (list1.isEmpty() && list2.isEmpty()) {
            return 0.0;
        }
        
        Set<Integer> set1 = new HashSet<>(list1);
        Set<Integer> set2 = new HashSet<>(list2);
        
        Set<Integer> intersection = new HashSet<>(set1);
        intersection.retainAll(set2);
        
        Set<Integer> union = new HashSet<>(set1);
        union.addAll(set2);
        
        return (double) intersection.size() / union.size();
    }
}
