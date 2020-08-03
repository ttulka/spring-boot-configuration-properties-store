package com.ttulka.spring.boot.configstore;

import org.junit.jupiter.api.Test;
import org.springframework.boot.autoconfigure.AutoConfigurations;
import org.springframework.boot.context.annotation.UserConfigurations;
import org.springframework.boot.test.context.runner.ApplicationContextRunner;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

import static org.assertj.core.api.Assertions.assertThat;

class ConfigurationStoreAutoConfigTest {

    private final ApplicationContextRunner contextRunner = new ApplicationContextRunner()
            .withConfiguration(UserConfigurations.of(ConfigurationStoreEventListenerTestConfig.class))
            .withConfiguration(AutoConfigurations.of(ConfigurationStoreAutoConfig.class));

    @Test
    void eventBasedConfigurationPropertiesStore_exists() {
        contextRunner.run(context -> {
            assertThat(context).hasSingleBean(ConfigurationStore.class);
        });
    }

    @Test
    void applicationConfigurationStoreEventPublisher_exists() {
        contextRunner.run(context -> {
            assertThat(context).hasSingleBean(ConfigurationStoreEventPublisher.class);
        });
    }

    @Configuration
    static class ConfigurationStoreEventListenerTestConfig {
        @Bean
        ConfigurationStoreEventListener defaultConfigurationStoreEventListener() {
            return (name, value) -> { /* do nothing */ };
        }
    }
}
