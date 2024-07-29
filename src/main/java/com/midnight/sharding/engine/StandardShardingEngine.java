package com.midnight.sharding.engine;

import com.midnight.sharding.config.ShardingProperties;
import lombok.extern.slf4j.Slf4j;

import java.util.*;

@Slf4j
public class StandardShardingEngine implements ShardingEngine {
    private Map<String, List<String>> actualDatabaseNames = new HashMap<>();
    private Map<String, List<String>> actualTableNames = new HashMap<>();
    private Map<String, ShardingStrategy> databaseStrageys = new HashMap<>();
    private Map<String, ShardingStrategy> tableStrageys = new HashMap<>();


    public StandardShardingEngine(ShardingProperties properties) {
        Map<String, Set<String>> databaseNames = new HashMap<>();
        Map<String, Set<String>> tableNames = new HashMap<>();

        properties.getTables().forEach((table, tableProperties) -> {
            databaseStrageys.put(table, new HashShardingStrategy(tableProperties.getDatabaseStrategy()));
            tableStrageys.put(table, new HashShardingStrategy(tableProperties.getTableStrategy()));

            if (!databaseNames.containsKey(table)) {
                databaseNames.put(table, new LinkedHashSet<>());
            }

            tableProperties.getActualDataNodes().forEach(actualDataNode -> {
                String[] split = actualDataNode.split("\\.");
                String databaseName = split[0];
                String tableName = split[1];

                if (!tableNames.containsKey(databaseName)) {
                    tableNames.put(databaseName, new LinkedHashSet<>());
                }
                databaseNames.get(table).add(databaseName);
                tableNames.get(databaseName).add(tableName);
            });
        });

        databaseNames.forEach((table, names) -> actualDatabaseNames.put(table, new ArrayList<>(names)));
        tableNames.forEach((database, names) -> actualTableNames.put(database, new ArrayList<>(names)));
    }

    @Override
    public ShardingResult sharding(String sql, Object[] args) {
        SqlParser parser = new SqlParser();
        SqlParser.SqlSchema schema = parser.parse(sql, args);
        String tableName = schema.getTableName();
        Map<String, Object> shardingColumnsMap = schema.getShardingColumnsMap();

        ShardingStrategy databaseStrategy = databaseStrageys.get(tableName);
        String targetDatabase = databaseStrategy.doSharding(actualDatabaseNames.get(tableName), tableName, shardingColumnsMap);

        ShardingStrategy tableStrategy = tableStrageys.get(tableName);
        String targetTable = tableStrategy.doSharding(actualTableNames.get(targetDatabase), tableName, shardingColumnsMap);

        log.info(" ===>>> target db.table = " + targetDatabase + "." + targetTable);

        ShardingResult result = new ShardingResult();
        result.setTargetDataSourceName(targetDatabase);
        result.setTargetSqlStatement(sql.replace(tableName, targetTable));
        result.setParameters(args);

        return result;
    }
}
