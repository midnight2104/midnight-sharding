package com.midnight.sharding.mybatis;

import com.midnight.sharding.engine.ShardingContext;
import com.midnight.sharding.engine.ShardingResult;
import lombok.extern.slf4j.Slf4j;
import org.apache.ibatis.executor.statement.StatementHandler;
import org.apache.ibatis.mapping.BoundSql;
import org.apache.ibatis.plugin.Interceptor;
import org.apache.ibatis.plugin.Intercepts;
import org.apache.ibatis.plugin.Invocation;
import org.springframework.objenesis.instantiator.util.UnsafeUtils;
import org.springframework.stereotype.Component;
import sun.misc.Unsafe;

import java.lang.reflect.Field;

@Slf4j
@Component
@Intercepts(@org.apache.ibatis.plugin.Signature(
        type = StatementHandler.class,
        method = "prepare",
        args = {java.sql.Connection.class, Integer.class}))
public class SqlStatementInterceptor implements Interceptor {
    @Override
    public Object intercept(Invocation invocation) throws Throwable {

        ShardingResult result = ShardingContext.get();
        if (result == null) {
            invocation.proceed();
        }

        StatementHandler handler = (StatementHandler) invocation.getTarget();
        BoundSql boundSql = handler.getBoundSql();

        log.info("====>  sql statement: " + boundSql.getSql());
        log.info("====>  sql param: " + boundSql.getParameterObject());

        String targetSql = result.getTargetSqlStatement();
        if (targetSql.equalsIgnoreCase(boundSql.getSql())) {
            return invocation.proceed();
        }

        replaceSql(boundSql, targetSql);

        return invocation.proceed();
    }

    private void replaceSql(BoundSql boundSql, String targetSql) throws NoSuchFieldException {
        Field field = boundSql.getClass().getDeclaredField("sql");
        Unsafe unsafe = UnsafeUtils.getUnsafe();
        long fieldOffset = unsafe.objectFieldOffset(field);
        unsafe.putObject(boundSql, fieldOffset, targetSql);
    }
}
