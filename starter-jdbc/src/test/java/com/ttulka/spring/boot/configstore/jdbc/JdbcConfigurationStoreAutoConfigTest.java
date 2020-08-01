package com.ttulka.spring.boot.configstore.jdbc;

import com.ttulka.spring.boot.configstore.ConfigurationStoreEventListener;
import org.junit.jupiter.api.Test;
import org.springframework.boot.autoconfigure.AutoConfigurations;
import org.springframework.boot.context.annotation.UserConfigurations;
import org.springframework.boot.test.context.runner.ApplicationContextRunner;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.jdbc.datasource.AbstractDataSource;

import javax.sql.DataSource;
import java.sql.Connection;

import static org.assertj.core.api.Assertions.assertThat;

class JdbcConfigurationStoreAutoConfigTest {

    private final ApplicationContextRunner contextRunner = new ApplicationContextRunner()
            .withConfiguration(UserConfigurations.of(DataSourceTestConfig.class))
            .withConfiguration(AutoConfigurations.of(JdbcConfigurationStoreAutoConfig.class));

    private final ApplicationContextRunner contextRunnerDisabled = new ApplicationContextRunner()
            .withPropertyValues("spring.configstore.jdbc.enabled=false")
            .withConfiguration(UserConfigurations.of(DataSourceTestConfig.class))
            .withConfiguration(AutoConfigurations.of(JdbcConfigurationStoreAutoConfig.class));

    @Test
    void jdbcConfigurationStoreEventListener_exists() {
        this.contextRunner.run(context -> {
            assertThat(context).hasSingleBean(ConfigurationStoreEventListener.class);
        });
    }

    @Test
    void updateConfigurationProperty_exists() {
        this.contextRunner.run(context -> {
            assertThat(context).hasSingleBean(UpdateConfigurationProperty.class);
        });
    }

    @Test
    void initDatabase_exists() {
        this.contextRunner.run(context -> {
            assertThat(context).hasSingleBean(InitDatabase.class);
        });
    }

    @Test
    void jdbcConfigStoreConfigurationProperties_exists() {
        this.contextRunner.run(context -> {
            assertThat(context).hasSingleBean(JdbcConfigStoreConfigurationProperties.class);
        });
    }

    @Test
    void jdbcTemplate_exists() {
        this.contextRunner.run(context -> {
            assertThat(context).hasSingleBean(JdbcTemplate.class);
        });
    }

    @Test
    void jdbcConfigurationStoreEventListener_disabled() {
        this.contextRunnerDisabled.run(context -> {
            assertThat(context).hasNotFailed();
            assertThat(context.containsBean("jdbcConfigurationStoreEventListener")).isFalse();
        });
    }

    @Configuration
    static class DataSourceTestConfig {
        @Bean
        DataSource mockDataSource() {
            return new AbstractDataSource() {
                @Override
                public Connection getConnection() {
                    return null;
                }

                @Override
                public Connection getConnection(String s, String s1) {
                    return null;
                }
            };
        }
    }
}
