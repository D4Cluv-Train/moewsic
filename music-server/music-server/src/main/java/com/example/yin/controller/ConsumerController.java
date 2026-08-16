package com.example.yin.controller;

import com.alibaba.fastjson.JSONObject;
import com.example.yin.domain.Consumer;
import com.example.yin.service.impl.ConsumerServiceImpl;
import com.example.yin.constant.Constants;
import com.example.yin.util.UploadUtil;
import org.apache.commons.lang3.StringUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Configuration;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;
import org.springframework.web.servlet.config.annotation.ResourceHandlerRegistry;
import org.springframework.web.servlet.config.annotation.WebMvcConfigurer;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpSession;
import java.io.IOException;
import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.Date;

@RestController
@Controller
public class ConsumerController {

    @Autowired
    private ConsumerServiceImpl consumerService;

    @Configuration
    public class MyPicConfig implements WebMvcConfigurer {
        @Override
        public void addResourceHandlers(ResourceHandlerRegistry registry) {
            String os = System.getProperty("os.name");
            if (os.toLowerCase().startsWith("win")) { // windos系统
                registry.addResourceHandler("/img/avatorImages/**")
                        .addResourceLocations("file:" + Constants.RESOURCE_WIN_PATH + "\\img\\avatorImages\\");
            } else { // MAC、Linux系统
                registry.addResourceHandler("/img/avatorImages/**")
                        .addResourceLocations("file:" + Constants.RESOURCE_MAC_PATH + "/img/avatorImages/");
            }
        }
    }

//    添加用户
    @ResponseBody
    @RequestMapping(value = "/user/add", method = RequestMethod.POST)
    public Object addUser(HttpServletRequest req){
        JSONObject jsonObject = new JSONObject();
        String username = StringUtils.trimToEmpty(req.getParameter("username"));
        String password = StringUtils.trimToEmpty(req.getParameter("password"));
        String sex = StringUtils.trimToEmpty(req.getParameter("sex"));
        String phoneNum = StringUtils.trimToEmpty(req.getParameter("phone_num"));
        String email = StringUtils.trimToEmpty(req.getParameter("email"));
        String birth = StringUtils.trimToEmpty(req.getParameter("birth"));
        String introduction = StringUtils.trimToEmpty(req.getParameter("introduction"));
        String location = StringUtils.trimToEmpty(req.getParameter("location"));
        String avator = StringUtils.trimToEmpty(req.getParameter("avator"));

        if (username.isEmpty() || password.isEmpty()) {
            jsonObject.put("code", 0);
            jsonObject.put("msg", "用户名或密码不能为空");
            return jsonObject;
        }
        if (consumerService.existUser(username)) {
            jsonObject.put("code", 0);
            jsonObject.put("msg", "用户名已存在");
            return jsonObject;
        }
        Consumer consumer = new Consumer();
        DateFormat dateFormat = new SimpleDateFormat("yyyy-MM-dd");
        Date myBirth = new Date();
        try {
            myBirth = dateFormat.parse(birth);
        } catch (Exception e){
            e.printStackTrace();
        }
        consumer.setUsername(username);
        consumer.setPassword(password);
        try {
            consumer.setSex(new Byte(sex));
        } catch (Exception e) {
            jsonObject.put("code", 0);
            jsonObject.put("msg", "性别参数错误");
            return jsonObject;
        }
        if (phoneNum.isEmpty()) {
            consumer.setPhoneNum(null);
        } else{
            consumer.setPhoneNum(phoneNum);
        }

        if (email.isEmpty()) {
            consumer.setEmail(null);
        } else{
            consumer.setEmail(email);
        }
        consumer.setBirth(myBirth);
        consumer.setIntroduction(introduction);
        consumer.setLocation(location);
        consumer.setAvator(avator);
        consumer.setCreateTime(new Date());
        consumer.setUpdateTime(new Date());

        boolean res = consumerService.addUser(consumer);
        if (res) {
            jsonObject.put("code", 1);
            jsonObject.put("msg", "注册成功");
            return jsonObject;
        } else {
            jsonObject.put("code", 0);
            jsonObject.put("msg", "注册失败");
            return jsonObject;
        }
    }

//    判断是否登录成功
    @ResponseBody
    @RequestMapping(value = "/user/login/status", method = RequestMethod.POST)
    public Object loginStatus(HttpServletRequest req, HttpSession session){

        JSONObject jsonObject = new JSONObject();
        String username = req.getParameter("username");
        String password = req.getParameter("password");
        boolean res = consumerService.veritypasswd(username, password);

        if (res){
            jsonObject.put("code", 1);
            jsonObject.put("msg", "登录成功");
            jsonObject.put("userMsg", consumerService.loginStatus(username));
            session.setAttribute("username", username);
            return jsonObject;
        }else {
            jsonObject.put("code", 0);
            jsonObject.put("msg", "用户名或密码错误");
            return jsonObject;
        }

    }

//    返回所有用户
    @RequestMapping(value = "/user", method = RequestMethod.GET)
    public Object allUser(){
        return consumerService.allUser();
    }

//    返回指定ID的用户
    @RequestMapping(value = "/user/detail", method = RequestMethod.GET)
    public Object userOfId(HttpServletRequest req){
        JSONObject jsonObject = new JSONObject();
        try {
            String id = req.getParameter("id");
            return consumerService.userOfId(Integer.parseInt(id));
        } catch (Exception e) {
            jsonObject.put("code", 0);
            jsonObject.put("msg", "参数错误");
            return jsonObject;
        }
    }

//    删除用户
    @RequestMapping(value = "/user/delete", method = RequestMethod.GET)
    public Object deleteUser(HttpServletRequest req){
        JSONObject jsonObject = new JSONObject();
        try {
            String id = req.getParameter("id");
            return consumerService.deleteUser(Integer.parseInt(id));
        } catch (Exception e) {
            jsonObject.put("code", 0);
            jsonObject.put("msg", "参数错误");
            return jsonObject;
        }
    }

//    更新用户信息
    @ResponseBody
    @RequestMapping(value = "/user/update", method = RequestMethod.POST)
    public Object updateUserMsg(HttpServletRequest req){
        JSONObject jsonObject = new JSONObject();
        String id = StringUtils.trimToEmpty(req.getParameter("id"));
        String username = StringUtils.trimToEmpty(req.getParameter("username"));
        String password = StringUtils.trimToEmpty(req.getParameter("password"));
        String sex = StringUtils.trimToEmpty(req.getParameter("sex"));
        String phoneNum = StringUtils.trimToEmpty(req.getParameter("phone_num"));
        String email = StringUtils.trimToEmpty(req.getParameter("email"));
        String birth = StringUtils.trimToEmpty(req.getParameter("birth"));
        String introduction = StringUtils.trimToEmpty(req.getParameter("introduction"));
        String location = StringUtils.trimToEmpty(req.getParameter("location"));

        if (id.isEmpty() || username.isEmpty()) {
            jsonObject.put("code", 0);
            jsonObject.put("msg", "参数错误");
            return jsonObject;
        }
        Consumer consumer = new Consumer();
        DateFormat dateFormat = new SimpleDateFormat("yyyy-MM-dd");
        Date myBirth = new Date();
        try {
            myBirth = dateFormat.parse(birth);
        }catch (Exception e){
            e.printStackTrace();
        }
        try {
            consumer.setId(Integer.parseInt(id));
            consumer.setSex(new Byte(sex));
        } catch (Exception e) {
            jsonObject.put("code", 0);
            jsonObject.put("msg", "参数错误");
            return jsonObject;
        }
        consumer.setUsername(username);
        consumer.setPassword(password);
        consumer.setSex(new Byte(sex));
        consumer.setPhoneNum(phoneNum);
        consumer.setEmail(email);
        consumer.setBirth(myBirth);
        consumer.setIntroduction(introduction);
        consumer.setLocation(location);
        consumer.setUpdateTime(new Date());

        boolean res = consumerService.updateUserMsg(consumer);
        if (res){
            jsonObject.put("code", 1);
            jsonObject.put("msg", "修改成功");
            return jsonObject;
        }else {
            jsonObject.put("code", 0);
            jsonObject.put("msg", "修改失败");
            return jsonObject;
        }
    }

//    更新用户头像
    @ResponseBody
    @RequestMapping(value = "/user/avatar/update", method = RequestMethod.POST)
    public Object updateUserPic(@RequestParam("file") MultipartFile avatorFile, @RequestParam("id")int id){
        JSONObject jsonObject = new JSONObject();

        if (avatorFile.isEmpty()) {
            jsonObject.put("code", 0);
            jsonObject.put("msg", "文件上传失败！");
            return jsonObject;
        }
        String filePath = System.getProperty("user.dir") + System.getProperty("file.separator") + "img" + System.getProperty("file.separator") + "avatorImages";
        String fileName;
        try {
            fileName = UploadUtil.storeImage(avatorFile, filePath);
        } catch (IllegalArgumentException e) {
            jsonObject.put("code", 0);
            jsonObject.put("msg", e.getMessage());
            return jsonObject;
        } catch (IOException e) {
            jsonObject.put("code", 0);
            jsonObject.put("msg", "上传失败: " + e.getMessage());
            return jsonObject;
        }

        String storeAvatorPath = "/img/avatorImages/" + fileName;
        Consumer consumer = new Consumer();
        consumer.setId(id);
        consumer.setAvator(storeAvatorPath);
        boolean res = consumerService.updateUserAvator(consumer);
        if (res){
            jsonObject.put("code", 1);
            jsonObject.put("avator", storeAvatorPath);
            jsonObject.put("msg", "上传成功");
            return jsonObject;
        }else {
            jsonObject.put("code", 0);
            jsonObject.put("msg", "上传失败");
            return jsonObject;
        }
    }
}
