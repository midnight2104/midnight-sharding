package com.midnight.sharding.demo;

import com.midnight.sharding.config.ShardingAutoConfiguration;
import com.midnight.sharding.demo.mapper.OrderMapper;
import com.midnight.sharding.demo.model.Order;
import com.midnight.sharding.mybatis.ShardingMapperFactoryBean;
import com.midnight.sharding.demo.model.User;
import com.midnight.sharding.demo.mapper.UserMapper;
import org.mybatis.spring.annotation.MapperScan;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.ApplicationRunner;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Import;

@SpringBootApplication
@Import(ShardingAutoConfiguration.class)
@MapperScan(value = "com.midnight.sharding.demo.mapper",
        factoryBean = ShardingMapperFactoryBean.class)
public class MidnightShardingApplication {

    public static void main(String[] args) {
        SpringApplication.run(MidnightShardingApplication.class, args);
    }


    @Autowired
    UserMapper userMapper;
    @Autowired
    OrderMapper orderMapper;

    @Bean
    ApplicationRunner applicationRunner() {
        return x -> {
//            System.out.println(" ===> ===> ===> ===> ===> ===>...");
//            System.out.println(" ===>   test user mapper  ===> ...");
//            System.out.println(" ===> ===> ===> ===> ===> ===>...");
//            for (int id = 1; id <= 10; id++) {
//                testUserMapper(id);
//            }
//            System.out.println(" ===> ===> ===> ===> ===> ===>...");
//            System.out.println(" ===> ===> ===> ===> ===> ===>...");
//            System.out.println(" ===> ===> ===> ===> ===> ===>...");

            System.out.println(" ===> ===> ===> ===> ===> ===>...");
            System.out.println(" ===>  test order mapper  ===> ...");
            System.out.println(" ===> ===> ===> ===> ===> ===>...");
            for (int id = 1; id <= 10; id++) {
                testOrderMapper(id);
            }
            System.out.println(" ===> ===> ===> ===> ===> ===>...");
            System.out.println(" ===> ===> ===> ===> ===> ===>...");
            System.out.println(" ===> ===> ===> ===> ===> ===>...");

        };
    }

    private void testUserMapper(int id) {
        System.out.println("\n ===> ===> ===>  id = " + id + "===> ===> ===>\n");
        System.out.println(" ===> 1. test insert ...");
        int inserted = userMapper.insert(new User(id, "midnight", 20));
        System.out.println(" ===> inserted = " + inserted);

        System.out.println(" ===> 2. test find ...");
        User user = userMapper.findById(id);
        System.out.println(" ===> find = " + user);

        System.out.println(" ===> 3. test update ...");
        user.setName("KK");
        int updated = userMapper.update(user);
        System.out.println(" ===> updated = " + updated);

        System.out.println(" ===> 4. test new find ...");
        User user2 = userMapper.findById(id);
        System.out.println(" ===> find = " + user2);

        System.out.println(" ===> 5. test delete ...");
        int deleted = userMapper.delete(id);
        System.out.println(" ===> deleted = " + deleted);
    }

    private void testOrderMapper(int id) {

        System.out.println("\n ===> ===> ===>  id = " + id + "===> ===> ===>\n");
        System.out.println(" ===> 1. test insert ...");
        int id2 = id+100;
        int inserted = orderMapper.insert(new Order(id, 1, 10d));
        System.out.println(" ===> inserted = " + inserted);
        inserted = orderMapper.insert(new Order(id2, 2, 20d));
        System.out.println(" ===> inserted = " + inserted);

        System.out.println(" ===> 2. test find ...");
        Order order1 = orderMapper.findById(id, 1);
        System.out.println(" ===> find = " + order1);
        Order order2 = orderMapper.findById(id2, 2);
        System.out.println(" ===> find = " + order2);

        System.out.println(" ===> 3. test update ...");
        order1.setPrice(11d);
        int updated = orderMapper.update(order1);
        System.out.println(" ===> updated = " + updated);
        order2.setPrice(22d);
        updated = orderMapper.update(order2);
        System.out.println(" ===> updated = " + updated);

        System.out.println(" ===> 4. test new find ...");
        Order order11 = orderMapper.findById(id, 1);
        System.out.println(" ===> find = " + order11);
        Order order22 = orderMapper.findById(id2, 2);
        System.out.println(" ===> find = " + order22);

        System.out.println(" ===> 5. test delete ...");
        int deleted = orderMapper.delete(id, 1);
        System.out.println(" ===> deleted = " + deleted);
        deleted = orderMapper.delete(id2, 2);
        System.out.println(" ===> deleted = " + deleted);
    }


}
