package com.example.yin.service.impl;

import com.example.yin.dao.ConsumerMapper;
import com.example.yin.domain.Consumer;
import com.example.yin.service.ConsumerService;
import com.example.yin.util.PasswordUtil;
import org.apache.commons.lang3.StringUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
public class ConsumerServiceImpl implements ConsumerService {

    @Autowired
    private ConsumerMapper consumerMapper;

    @Override
    public boolean addUser(Consumer consumer) {
        if (StringUtils.isNotBlank(consumer.getPassword())) {
            consumer.setPassword(PasswordUtil.hash(consumer.getPassword()));
        }
        return consumerMapper.insertSelective(consumer) > 0 ? true : false;
    }

    @Override
    public boolean updateUserMsg(Consumer consumer) {
        String password = consumer.getPassword();
        if (StringUtils.isBlank(password)) {
            // 未填写密码则不更新，避免把已有密码清空
            consumer.setPassword(null);
        } else if (!PasswordUtil.isHash(password)) {
            consumer.setPassword(PasswordUtil.hash(password));
        }
        return consumerMapper.updateUserMsg(consumer) > 0 ? true : false;
    }

    @Override
    public boolean updateUserAvator(Consumer consumer) {
        return consumerMapper.updateUserAvator(consumer) > 0 ? true : false;
    }

    @Override
    public boolean existUser(String username) {
        return consumerMapper.existUsername(username) > 0 ? true : false;
    }

    @Override
    public boolean veritypasswd(String username, String password) {
        Consumer consumer = queryByUsername(username);
        if (consumer == null || consumer.getPassword() == null) {
            return false;
        }
        boolean matched = PasswordUtil.verify(password, consumer.getPassword());
        if (matched && !PasswordUtil.isHash(consumer.getPassword())) {
            // 历史明文密码，登录成功后升级为哈希存储
            consumerMapper.updatePassword(username, PasswordUtil.hash(password));
        }
        return matched;
    }

    private Consumer queryByUsername(String username) {
        List<Consumer> list = consumerMapper.loginStatus(username);
        return (list == null || list.isEmpty()) ? null : list.get(0);
    }

    @Override
    public boolean deleteUser(Integer id) {
        return consumerMapper.deleteUser(id) > 0 ? true : false;
    }

    @Override
    public List<Consumer> allUser() {
        return consumerMapper.allUser();
    }

    @Override
    public List<Consumer> userOfId(Integer id) {
        return consumerMapper.userOfId(id);
    }

    @Override
    public List<Consumer> loginStatus(String username) {
        return consumerMapper.loginStatus(username);
    }
}
