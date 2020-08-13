package com.ttulka.spring.boot.configstore.jdbc;

import com.ttulka.spring.boot.configstore.ConfigurationStorePostProcessor;
import org.springframework.boot.jdbc.DataSourceBuilder;
import org.springframework.core.env.Environment;
import org.springframework.jdbc.BadSqlGrammarException;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.util.StringUtils;

import javax.sql.DataSource;
import java.util.Map;

/**
 * The post-processor for loading configuration properties from a JDBC source.
 * Expected a Spring configuration properties (spring.datasource) for a data source in environment.
 */
class JdbcConfigurationStorePostProcessor extends ConfigurationStorePostProcessor {

    @Override
    protected Map<String, Object> propertySource(Environment env) {
        if (env.getProperty("spring.configstore.jdbc.enabled", "true").equalsIgnoreCase("true")) {
            try {
                return propertiesFromJdbc(env);

            } catch (BadSqlGrammarException ignore) {
                // this could happen when the app starts for the first time
                // and the db schema is not initialized yet
            }
        }
        return Map.of();
    }

    private Map<String, Object> propertiesFromJdbc(Environment env) {
        DataSource dataSource = DataSourceBuilder.create()
                .url(env.getProperty("spring.datasource.url"))
                .username(env.getProperty("spring.datasource.username"))
                .password(env.getProperty("spring.datasource.password"))
                .driverClassName(env.getProperty("spring.datasource.driver-class-name"))
                .build();

        String schema = env.getProperty("spring.configstore.jdbc.schema");
        String table = env.getProperty("spring.configstore.jdbc.table", JdbcConfigStoreConfigurationProperties.TABLE_DEFAULT);
        String tableName = StringUtils.hasLength(schema) ? schema + "." + table : table;

        return new FindConfigurationProperties(tableName, new JdbcTemplate(dataSource)).all();
    }
}
