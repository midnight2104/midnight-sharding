package com.midnight.sharding.controller;

import com.midnight.sharding.entity.User;
import com.midnight.sharding.service.UserService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.autoconfigure.EnableAutoConfiguration;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;

@RestController
@EnableAutoConfiguration
public class UserController {

    @Autowired
    UserService userService;
    
    @RequestMapping("/user/find")
    User find(Integer id) {
        return userService.find(id);
    }

    @RequestMapping("/user/list")
    List<User> list() {
        return userService.list();

    }

    @RequestMapping("/user/find2")
    User find2(Integer id, String name) {
        return userService.find2(id, name);
    }

}