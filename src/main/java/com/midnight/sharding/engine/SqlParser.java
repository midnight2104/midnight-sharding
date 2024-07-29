package com.midnight.sharding.engine;

import com.alibaba.druid.sql.SQLUtils;
import com.alibaba.druid.sql.ast.SQLExpr;
import com.alibaba.druid.sql.ast.SQLName;
import com.alibaba.druid.sql.ast.SQLStatement;
import com.alibaba.druid.sql.ast.expr.SQLIdentifierExpr;
import com.alibaba.druid.sql.ast.statement.SQLInsertStatement;
import com.alibaba.druid.sql.dialect.mysql.visitor.MySqlSchemaStatVisitor;
import com.alibaba.druid.stat.TableStat;
import lombok.Data;

import java.util.*;

/**
 * parser for sql.
 *
 * @Author : kimmking(kimmking@apache.org)
 * @create 2024/7/27 下午12:38
 */
public class SqlParser {
    public SqlSchema parse(String sql, Object[] args) {
        SQLStatement sqlStatement = SQLUtils.parseSingleMysqlStatement(sql);
        if(sqlStatement instanceof SQLInsertStatement sqlInsertStatement) {
            SqlSchema schema = new SqlSchema();
            schema.setTableName(sqlInsertStatement.getTableName().getSimpleName());
            schema.setShardingColumnsMap(findInsertShardingColumns(sqlInsertStatement, args));
            return schema;
        } else {
            return visit(sqlStatement, args);
        }
    }

    public SqlSchema visit(SQLStatement statement, Object[] args) {
        MySqlSchemaStatVisitor visitor = new MySqlSchemaStatVisitor();
        visitor.setParameters(Arrays.asList(args));
        statement.accept(visitor);
        List<SQLName> names = new ArrayList<>(new LinkedHashSet<>(visitor.getOriginalTables()));
        if(names.size() > 1) {
            throw new RuntimeException("not support multi table sharding");
        }
        SqlSchema schema = new SqlSchema();
        schema.setTableName(names.get(0).getSimpleName());
        schema.setShardingColumnsMap(findShardingColumns(visitor));
        return schema;
    }

    private Map<String, Object> findShardingColumns(MySqlSchemaStatVisitor visitor) {
        Map<String, Object> shardingColumnsMap = new HashMap<>();
        for (TableStat.Condition condition : visitor.getConditions()) {
            shardingColumnsMap.put(condition.getColumn().getName(), condition.getValues().toArray()[0]);
        }
        return shardingColumnsMap;
    }

    private Map<String, Object> findInsertShardingColumns(SQLInsertStatement statement, Object[] args) {
        Map<String, Object> shardingColumnsMap = new HashMap<>();
        List<SQLExpr> columns = statement.getColumns();
        for (int i = 0; i < columns.size(); i++) {
            SQLIdentifierExpr columnExpr = (SQLIdentifierExpr) columns.get(i);
            shardingColumnsMap.put(columnExpr.getSimpleName(), args[i]);
        }
        return shardingColumnsMap;
    }

    @Data
    public static class SqlSchema {
        private String tableName;
        private Map<String, Object> shardingColumnsMap;
    }

}
