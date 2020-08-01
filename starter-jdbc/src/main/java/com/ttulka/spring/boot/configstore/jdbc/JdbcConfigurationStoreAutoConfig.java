package com.ttulka.spring.boot.configstore.jdbc;

import com.ttulka.spring.boot.configstore.ConfigurationStoreAutoConfig;
import com.ttulka.spring.boot.configstore.ConfigurationStoreEventListener;
import org.springframework.boot.autoconfigure.AutoConfigureAfter;
import org.springframework.boot.autoconfigure.condition.ConditionalOnBean;
import org.springframework.boot.autoconfigure.condition.ConditionalOnMissingBean;
import org.springframework.boot.autoconfigure.condition.ConditionalOnProperty;
import org.springframework.boot.autoconfigure.jdbc.DataSourceTransactionManagerAutoConfiguration;
import org.springframework.boot.autoconfigure.jdbc.JdbcTemplateAutoConfiguration;
import org.springframework.boot.context.properties.EnableConfigurationProperties;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.Import;
import org.springframework.jdbc.core.JdbcTemplate;

import javax.sql.DataSource;

@ConditionalOnProperty(prefix = "spring.configstore.jdbc", name = "enabled", havingValue = "true", matchIfMissing = true)
@ConditionalOnBean(DataSource.class)
@AutoConfigureAfter({JdbcTemplateAutoConfiguration.class, DataSourceTransactionManagerAutoConfiguration.class})
@EnableConfigurationProperties(JdbcConfigStoreConfigurationProperties.class)
@Import(ConfigurationStoreAutoConfig.class)
@Configuration
class JdbcConfigurationStoreAutoConfig {

    @Bean
    ConfigurationStoreEventListener jdbcConfigurationStoreEventListener(
            UpdateConfigurationProperty updateConfigurationProperty) {
        return (name, value) -> updateConfigurationProperty.update(name, value);
    }

    @Bean
    UpdateConfigurationProperty updateConfigurationProperty(
            JdbcConfigStoreConfigurationProperties conf, JdbcTemplate jdbcTemplate) {
        return new UpdateConfigurationProperty(conf.tableName(), jdbcTemplate);
    }

    @Bean
    InitDatabase initDatabase(JdbcConfigStoreConfigurationProperties conf, JdbcTemplate jdbcTemplate) {
        return new InitDatabase(conf.getSchema(), conf.getTable(), jdbcTemplate);
    }

    @ConditionalOnMissingBean(JdbcTemplate.class)
    @Configuration
    static class JdbcTemplateFallbackConfig {

        @Bean
        JdbcTemplate fallbackJdbcTemplate(DataSource dataSource) {
            return new JdbcTemplate(dataSource);
        }
    }
}
