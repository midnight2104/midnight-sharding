package com.midnight.sharding.engine;

import com.alibaba.druid.sql.SQLUtils;
import com.alibaba.druid.sql.ast.SQLExpr;
import com.alibaba.druid.sql.ast.SQLName;
import com.alibaba.druid.sql.ast.SQLStatement;
import com.alibaba.druid.sql.ast.expr.SQLIdentifierExpr;
import com.alibaba.druid.sql.ast.statement.SQLInsertStatement;
import com.alibaba.druid.sql.dialect.mysql.visitor.MySqlSchemaStatVisitor;
import com.midnight.sharding.config.ShardingProperties;
import com.midnight.sharding.strategy.HashShardingStrategy;
import com.midnight.sharding.strategy.ShardingStrategy;
import lombok.extern.slf4j.Slf4j;
import org.springframework.util.LinkedMultiValueMap;
import org.springframework.util.MultiValueMap;

import java.util.HashMap;
import java.util.LinkedHashSet;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

@Slf4j
public class StandardShardingEngine implements ShardingEngine {
    private final MultiValueMap<String, String> actualDatabaseNames = new LinkedMultiValueMap<>();
    private final MultiValueMap<String, String> actualTableNames = new LinkedMultiValueMap<>();
    private Map<String, ShardingStrategy> databaseStrageys = new HashMap<>();
    private Map<String, ShardingStrategy> tableStrageys = new HashMap<>();


    public StandardShardingEngine(ShardingProperties properties) {

        properties.getTables().forEach((table, tableProperties) -> {
            databaseStrageys.put(table, new HashShardingStrategy(tableProperties.getDatabaseStrategy()));
            tableStrageys.put(table, new HashShardingStrategy(tableProperties.getTableStrategy()));
            tableProperties.getActualDataNodes().forEach(actualDataNode -> {
                String[] split = actualDataNode.split("\\.");
                String databaseName = split[0];
                String tableName = split[1];

                actualDatabaseNames.add(databaseName, tableName);
                actualTableNames.add(tableName, databaseName);

            });
            databaseStrageys.put(table, new HashShardingStrategy(tableProperties.getDatabaseStrategy()));
            tableStrageys.put(table, new HashShardingStrategy(tableProperties.getTableStrategy()));

        });

    }

    @Override
    public ShardingResult sharding(String sql, Object[] args) {
        SQLStatement sqlStatement = SQLUtils.parseSingleMysqlStatement(sql);

        String table;
        Map<String, Object> shardingColumnsMap;
        if (sqlStatement instanceof SQLInsertStatement sqlInsertStatement) {
            table = sqlInsertStatement.getTableName().getSimpleName();
            shardingColumnsMap = getShardingColumnsMap(sqlInsertStatement, args);
        } else {
            MySqlSchemaStatVisitor visitor = new MySqlSchemaStatVisitor();
            visitor.setParameters(List.of(args));
            sqlStatement.accept(visitor);

            LinkedHashSet<SQLName> sqlNames = new LinkedHashSet<>(visitor.getOriginalTables());
            if (sqlNames.size() > 1) {
                throw new RuntimeException("not support multi table sharding");
            }

            table = sqlNames.iterator().next().getSimpleName();
            log.info(" visitor.getOriginalTables = " + table);
            shardingColumnsMap = visitor.getConditions().stream()
                    .collect(Collectors.toMap(k -> k.getColumn().getName(), v -> v.getValues().get(0)));
            log.info(" visitor.getConditions = " + shardingColumnsMap);
        }

        ShardingStrategy databaseStrategy = databaseStrageys.get(table);
        String targetDatabase = databaseStrategy.doSharding(actualDatabaseNames.get(table), table, shardingColumnsMap);

        ShardingStrategy tableStrategy = tableStrageys.get(table);
        String targetTable = tableStrategy.doSharding(actualTableNames.get(targetDatabase), table, shardingColumnsMap);


        log.info(" ===>>> ");
        log.info(" ===>>> target db.table = " + targetDatabase + "." + targetTable);
        log.info(" ===>>> ");

        return new ShardingResult(targetDatabase, sql.replace(table, targetTable));
    }

    private Map<String, Object> getShardingColumnsMap(SQLInsertStatement sqlInsertStatement, Object[] args) {
        Map<String, Object> shardingColumnsMap = new HashMap<>();
        List<SQLExpr> columns = sqlInsertStatement.getColumns();
        for (int i = 0; i < columns.size(); i++) {
            SQLExpr column = columns.get(i);
            SQLIdentifierExpr columnExpr = (SQLIdentifierExpr) column;
            String columnName = columnExpr.getSimpleName();
            shardingColumnsMap.put(columnName, args[i]);
        }

        return shardingColumnsMap;
    }
}
