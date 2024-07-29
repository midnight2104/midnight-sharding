package com.midnight.sharding.engine;

public interface ShardingEngine {

    ShardingResult sharding(String sql,  Object[]  args);
}
