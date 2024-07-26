package com.midnight.sharding;

import lombok.extern.slf4j.Slf4j;
import org.apache.ibatis.executor.statement.StatementHandler;
import org.apache.ibatis.mapping.BoundSql;
import org.apache.ibatis.plugin.Interceptor;
import org.apache.ibatis.plugin.Intercepts;
import org.apache.ibatis.plugin.Invocation;
import org.springframework.stereotype.Component;

@Slf4j
@Component
@Intercepts(@org.apache.ibatis.plugin.Signature(
        type = StatementHandler.class,
        method = "prepare",
        args = {java.sql.Connection.class, Integer.class}))
public class SQLStatementInterceptor implements Interceptor {
    @Override
    public Object intercept(Invocation invocation) throws Throwable {
        StatementHandler handler = (StatementHandler) invocation.getTarget();
        BoundSql boundSql = handler.getBoundSql();
        log.info("====>  sql statement: " + boundSql.getSql());
        log.info("====>  sql param: " + boundSql.getParameterObject());
        return invocation.proceed();
    }
}
