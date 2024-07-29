package com.midnight.sharding.datasource;

import com.alibaba.druid.pool.DruidDataSourceFactory;
import com.midnight.sharding.config.ShardingProperties;
import com.midnight.sharding.engine.ShardingContext;
import com.midnight.sharding.engine.ShardingResult;
import lombok.extern.slf4j.Slf4j;
import org.springframework.jdbc.datasource.lookup.AbstractRoutingDataSource;

import java.util.LinkedHashMap;

@Slf4j
public class ShardingDataSource extends AbstractRoutingDataSource {

    public ShardingDataSource(ShardingProperties properties) {
        LinkedHashMap<Object, Object> dataSourceMap = new LinkedHashMap<>();

        // 创建数据源
        properties.getDatasources().forEach((k, v) -> {
            try {
                dataSourceMap.put(k, DruidDataSourceFactory.createDataSource(v));
            } catch (Exception e) {
                throw new RuntimeException(e);
            }
        });

        // 设置数据源
        setTargetDataSources(dataSourceMap);

        // 设置默认数据源，默认第一个
        setDefaultTargetDataSource(dataSourceMap.values().iterator().next());
    }

    @Override
    protected Object determineCurrentLookupKey() {
        ShardingResult shardingResult = ShardingContext.get();
        Object key = shardingResult == null ? null : shardingResult.getTargetDataSourceName();

        log.info(" ===>> determineCurrentLookupKey = " + key);
        return key;
    }
}
