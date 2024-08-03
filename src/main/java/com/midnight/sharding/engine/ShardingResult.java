package com.midnight.sharding.engine;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class ShardingResult {
    private String targetDataSourceName;
    private String targetSqlStatement;
}
