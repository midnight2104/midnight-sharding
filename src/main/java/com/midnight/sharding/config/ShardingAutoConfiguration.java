package com.midnight.sharding.config;

import com.midnight.sharding.datasource.ShardingDataSource;
import com.midnight.sharding.engine.ShardingEngine;
import com.midnight.sharding.engine.StandardShardingEngine;
import com.midnight.sharding.mybatis.SqlStatementInterceptor;
import org.springframework.boot.context.properties.EnableConfigurationProperties;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;


@Configuration
@EnableConfigurationProperties(ShardingProperties.class)
public class ShardingAutoConfiguration {

    @Bean
    public ShardingDataSource shardingDataSource(ShardingProperties properties) {
        return new ShardingDataSource(properties);
    }


    @Bean
    public ShardingEngine shardingEngine(ShardingProperties properties) {
        return new StandardShardingEngine(properties);
    }

    @Bean
    public SqlStatementInterceptor sqlStatementInterceptor() {
        return new SqlStatementInterceptor();
    }
}
