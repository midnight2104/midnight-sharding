package com.midnight.sharding.service;

import com.midnight.sharding.entity.User;

import java.util.List;

public interface UserService {

    User find(int id);

    List<User> list();

    User find2(int id, String name);

}