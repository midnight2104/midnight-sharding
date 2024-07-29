package com.midnight.sharding.mybatis;

import com.midnight.sharding.engine.ShardingContext;
import com.midnight.sharding.engine.ShardingEngine;
import com.midnight.sharding.engine.ShardingResult;
import lombok.Setter;
import lombok.extern.slf4j.Slf4j;
import org.apache.ibatis.mapping.BoundSql;
import org.apache.ibatis.mapping.MappedStatement;
import org.apache.ibatis.mapping.ParameterMapping;
import org.apache.ibatis.session.Configuration;
import org.apache.ibatis.session.SqlSession;
import org.mybatis.spring.mapper.MapperFactoryBean;
import org.springframework.util.ClassUtils;
import org.springframework.util.ReflectionUtils;

import java.lang.reflect.Field;
import java.lang.reflect.Method;
import java.lang.reflect.Proxy;
import java.util.List;

@Slf4j
public class ShardingMapperFactoryBean<T> extends MapperFactoryBean<T> {

    @Setter
    private ShardingEngine engine;

    public ShardingMapperFactoryBean() {

    }

    public ShardingMapperFactoryBean(Class<T> mapperInterface) {
        super(mapperInterface);
    }

    @SuppressWarnings("unchecked")
    @Override
    public T getObject() throws Exception {
        Object proxy = super.getObject();
        SqlSession session = getSqlSession();
        Configuration configuration = session.getConfiguration();
        Class<?> clazz = getMapperInterface();

        return (T) Proxy.newProxyInstance(clazz.getClassLoader(), new Class[]{clazz}, (p, method, args) -> {

            BoundSql boundSql = getBoundSql(method, args, clazz, configuration);
            Object[] params = getParams(args, boundSql);

            ShardingResult result = engine.sharding(boundSql.getSql(), params);
            ShardingContext.set(result);

            return method.invoke(proxy, args);
        });
    }

    private Object[] getParams(Object[] args, BoundSql boundSql) throws IllegalAccessException {
        Object[] params = args;

        if (args.length == 1 && !ClassUtils.isPrimitiveOrWrapper(args[0].getClass())) {
            Object arg = args[0];
            List<String> cols = boundSql.getParameterMappings().stream().map(ParameterMapping::getProperty).toList();

            Object[] values = new Object[cols.size()];

            for (int i = 0; i < cols.size(); i++) {
                Field field = ReflectionUtils.findField(arg.getClass(), cols.get(i));
                if (field == null) {
                    throw new IllegalArgumentException("can not find field " + cols.get(i));
                }

                field.setAccessible(true);
                values[i] = field.get(arg);
            }
            params = values;

        }
        return params;
    }

    private BoundSql getBoundSql(Method method, Object[] args, Class<?> clazz, Configuration configuration) {
        String mapperId = clazz.getName() + "." + method.getName();
        MappedStatement statement = configuration.getMappedStatement(mapperId);
        return statement.getBoundSql(args);
    }
}
