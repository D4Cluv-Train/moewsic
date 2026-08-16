package com.example.yin.service.impl;

import com.example.yin.dao.AdminMapper;
import com.example.yin.domain.Admin;
import com.example.yin.service.AdminService;
import com.example.yin.util.PasswordUtil;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

@Service
public class AdminServiceImpl implements AdminService {

    @Autowired
    private AdminMapper adminMapper;

    @Override
    public boolean veritypasswd(String name, String password) {
        Admin admin = adminMapper.getByName(name);
        if (admin == null || admin.getPassword() == null) {
            return false;
        }
        boolean matched = PasswordUtil.verify(password, admin.getPassword());
        if (matched && !PasswordUtil.isHash(admin.getPassword())) {
            // 历史明文密码，登录成功后升级为哈希存储
            adminMapper.updatePassword(name, PasswordUtil.hash(password));
        }
        return matched;
    }
}
