package com.midnight.sharding.engine;

import groovy.lang.Closure;

import java.util.Collections;
import java.util.List;
import java.util.Map;
import java.util.Properties;

public class HashShardingStrategy implements ShardingStrategy {
    private String shardingColumn;
    private String algorithmExpression;

    public HashShardingStrategy(Properties properties) {
        this.shardingColumn = properties.getProperty("shardingColumn");
        this.algorithmExpression = properties.getProperty("algorithmExpression");
    }


    @Override
    public List<String> getShardingColumns() {
        return Collections.singletonList(shardingColumn);
    }

    @Override
    public String doSharding(List<String> availableTargetNames, String logicTableName, Map<String, Object> shardingParams) {
        InlineExpressionParser parser = new InlineExpressionParser(InlineExpressionParser.handlePlaceHolder(algorithmExpression));
        Closure closure = parser.evaluateClosure();
        closure.setProperty(shardingColumn, shardingParams.get(shardingColumn));
        return closure.call().toString();
    }
}
