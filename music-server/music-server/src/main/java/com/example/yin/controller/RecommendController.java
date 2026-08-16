package com.example.yin.controller;

import com.alibaba.fastjson.JSONObject;
import com.example.yin.domain.Song;
import com.example.yin.domain.UserItem;
import com.example.yin.service.RecommendService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.*;

import javax.servlet.http.HttpServletRequest;
import java.util.List;

@RestController
@Controller
@RequestMapping("/recommend")
public class RecommendController {
    
    @Autowired
    private RecommendService recommendService;
    
    @RequestMapping(value = "/collaborative", method = RequestMethod.GET)
    public Object getCollaborativeFilteringRecommendations(HttpServletRequest req) {
        JSONObject jsonObject = new JSONObject();
        String userIdStr = req.getParameter("userId");
        String limitStr = req.getParameter("limit");
        
        if (userIdStr == null || userIdStr.trim().isEmpty()) {
            jsonObject.put("code", 0);
            jsonObject.put("msg", "用户ID不能为空");
            return jsonObject;
        }
        
        int userId = Integer.parseInt(userIdStr);
        int limit = limitStr != null && !limitStr.isEmpty() ? Integer.parseInt(limitStr) : 10;
        
        try {
            List<Song> recommendations = recommendService.getCollaborativeFilteringRecommendations(userId, limit);
            jsonObject.put("code", 1);
            jsonObject.put("msg", "推荐成功");
            jsonObject.put("data", recommendations);
            jsonObject.put("total", recommendations.size());
            return jsonObject;
        } catch (Exception e) {
            jsonObject.put("code", 0);
            jsonObject.put("msg", "推荐失败: " + e.getMessage());
            return jsonObject;
        }
    }
    
    @RequestMapping(value = "/content", method = RequestMethod.GET)
    public Object getContentBasedRecommendations(HttpServletRequest req) {
        JSONObject jsonObject = new JSONObject();
        String userIdStr = req.getParameter("userId");
        String limitStr = req.getParameter("limit");
        
        if (userIdStr == null || userIdStr.trim().isEmpty()) {
            jsonObject.put("code", 0);
            jsonObject.put("msg", "用户ID不能为空");
            return jsonObject;
        }
        
        int userId = Integer.parseInt(userIdStr);
        int limit = limitStr != null && !limitStr.isEmpty() ? Integer.parseInt(limitStr) : 10;
        
        try {
            List<Song> recommendations = recommendService.getContentBasedRecommendations(userId, limit);
            jsonObject.put("code", 1);
            jsonObject.put("msg", "推荐成功");
            jsonObject.put("data", recommendations);
            jsonObject.put("total", recommendations.size());
            return jsonObject;
        } catch (Exception e) {
            jsonObject.put("code", 0);
            jsonObject.put("msg", "推荐失败: " + e.getMessage());
            return jsonObject;
        }
    }
    
    @RequestMapping(value = "/popular", method = RequestMethod.GET)
    public Object getPopularSongs(HttpServletRequest req) {
        JSONObject jsonObject = new JSONObject();
        String limitStr = req.getParameter("limit");
        
        int limit = limitStr != null && !limitStr.isEmpty() ? Integer.parseInt(limitStr) : 10;
        
        try {
            List<Song> popularSongs = recommendService.getPopularSongs(limit);
            jsonObject.put("code", 1);
            jsonObject.put("msg", "获取成功");
            jsonObject.put("data", popularSongs);
            jsonObject.put("total", popularSongs.size());
            return jsonObject;
        } catch (Exception e) {
            jsonObject.put("code", 0);
            jsonObject.put("msg", "获取失败: " + e.getMessage());
            return jsonObject;
        }
    }
    
    @RequestMapping(value = "/matrix", method = RequestMethod.GET)
    public Object getUserItemMatrix() {
        JSONObject jsonObject = new JSONObject();
        
        try {
            List<UserItem> userItems = recommendService.getUserItemMatrix();
            jsonObject.put("code", 1);
            jsonObject.put("msg", "获取成功");
            jsonObject.put("data", userItems);
            jsonObject.put("total", userItems.size());
            return jsonObject;
        } catch (Exception e) {
            jsonObject.put("code", 0);
            jsonObject.put("msg", "获取失败: " + e.getMessage());
            return jsonObject;
        }
    }
}
