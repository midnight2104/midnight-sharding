package com.midnight.sharding.demo.mapper;

import com.midnight.sharding.demo.model.User;
import org.apache.ibatis.annotations.*;

/**
 * Mapper for user.
 *
 * @Author : kimmking(kimmking@apache.org)
 * @create 2024/7/25 下午8:07
 */

@Mapper
public interface UserMapper {

    @Insert("insert into user (id, name, age) values (#{id}, #{name}, #{age})")
    int insert(User user);

    @Select("select * from user where id = #{id}")
    User findById(int id);

    @Update("update user set name = #{name}, age = #{age} where id = #{id}")
    int update(User user);

    @Delete("delete from user where id = #{id}")
    int delete(int id);
}
