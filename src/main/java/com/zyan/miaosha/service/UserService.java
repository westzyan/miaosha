package com.zyan.miaosha.service;

import com.zyan.miaosha.dao.UserDao;
import com.zyan.miaosha.domain.User;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

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

    @Transactional
    public boolean tx() {
        User u1 = new User();
        u1.setId(2);
        u1.setName("222");
        userDao.inset(u1);

        User u2 = new User();
        u1.setId(1);
        u1.setName("333");
        userDao.inset(u2);

        return true;
    }
}
