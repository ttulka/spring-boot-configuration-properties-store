package com.ttulka.spring.boot.configstore.jdbc;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.env.EnvironmentPostProcessor;
import org.springframework.boot.jdbc.DataSourceBuilder;
import org.springframework.core.Ordered;
import org.springframework.core.env.ConfigurableEnvironment;
import org.springframework.core.env.MapPropertySource;
import org.springframework.core.env.MutablePropertySources;
import org.springframework.core.env.PropertySource;
import org.springframework.jdbc.BadSqlGrammarException;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.util.StringUtils;

import javax.sql.DataSource;

/**
 * The post-processor for loading configuration properties from a JDBC source.
 *
 * Expected a Spring configuration properties (spring.datasource) for a data source in environment.
 */
class JdbcConfigurationStorePostProcessor implements EnvironmentPostProcessor, Ordered {

    static final String PROPERTY_SOURCE_NAME = "propertiesFromJdbcConfigurationStore";

    @Override
    public void postProcessEnvironment(ConfigurableEnvironment env, SpringApplication application) {
        if (env.getProperty("spring.configstore.jdbc.enabled", "true").equalsIgnoreCase("false")) {
            return;
        }
        try {
            DataSource dataSource = DataSourceBuilder.create()
                    .url(env.getProperty("spring.datasource.url"))
                    .username(env.getProperty("spring.datasource.username"))
                    .password(env.getProperty("spring.datasource.password"))
                    .driverClassName(env.getProperty("spring.datasource.driver-class-name"))
                    .build();

            String schema = env.getProperty("spring.configstore.jdbc.schema");
            String table = env.getProperty("spring.configstore.jdbc.table", JdbcConfigStoreConfigurationProperties.TABLE_DEFAULT);
            String tableName = StringUtils.hasLength(schema) ? schema + "." + table : table;

            var newSource = new MapPropertySource(
                    PROPERTY_SOURCE_NAME,
                    new FindConfigurationProperties(tableName, new JdbcTemplate(dataSource)).all());

            if (env.getProperty("spring.configstore.source.last", "false").equalsIgnoreCase("TRUE")) {
                addAsLast(env.getPropertySources(), newSource);
            } else {
                addAsFirst(env.getPropertySources(), newSource);
            }

        } catch (BadSqlGrammarException ignore) {
        } catch (Exception e) {
            throw new RuntimeException(e);
        }
    }

    private void addAsFirst(MutablePropertySources sources, PropertySource<?> propertySource) {
        sources.addFirst(propertySource);
    }

    private void addAsLast(MutablePropertySources sources, PropertySource<?> propertySource) {
        sources.addLast(propertySource);
    }

    @Override
    public int getOrder() {
        return Ordered.LOWEST_PRECEDENCE;
    }
}
