package com.midnight.sharding.mapper;

import com.midnight.sharding.entity.User;
import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Select;

import java.util.List;

@Mapper
public interface UserMapper {

    @Select("select * from t_user where id = #{id}")
    User find(int id);

    @Select("select * from t_user")
    List<User> list();

    @Select("select * from t_user where id = #{id} and name=#{name}")
    User find2(int id, String name);

}