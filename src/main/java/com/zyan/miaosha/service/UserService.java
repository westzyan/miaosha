package com.zyan.miaosha.service;

import com.zyan.miaosha.dao.UserDao;
import com.zyan.miaosha.domain.User;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

/**
 * @author zyan
 * @version 1.0
 * @date 19-11-19 下午10:35
 */

@Service
public class UserService {

    @Autowired
    UserDao userDao;

    public User getById(int id){
        return userDao.getById(id);
    }
}
