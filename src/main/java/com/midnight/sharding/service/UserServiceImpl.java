package com.midnight.sharding.service;

import com.midnight.sharding.entity.User;

import com.midnight.sharding.mapper.UserMapper;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;


@Service
public class UserServiceImpl implements UserService {

    @Autowired
    UserMapper userMapper; //DAO  // Repository

    // 开启spring cache
    public User find(int id) {
        System.out.println(" ==> find " + id);
        return userMapper.find(id);
    }

    public List<User> list(){
        return userMapper.list();
    }

    @Override
    public User find2(int id, String name) {
        return userMapper.find2(id,name);
    }


}