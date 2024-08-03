package com.midnight.sharding.strategy;

import java.util.List;
import java.util.Map;


public interface ShardingStrategy {

    List<String> getShardingColums();

    String doSharding(List<String> availableTargetNames, String logicTableName, Map<String, Object> shardingParams);

}
