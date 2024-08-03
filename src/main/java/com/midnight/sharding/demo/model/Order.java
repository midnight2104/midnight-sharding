package com.midnight.sharding.demo.model;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

/**
 * Order entity.
 *
 * @Author : kimmking(kimmking@apache.org)
 * @create 2024/7/29 下午11:56
 */
@AllArgsConstructor
@NoArgsConstructor
@Data
public class Order {
    private int id;
    private int uid;
    private double price;
}
