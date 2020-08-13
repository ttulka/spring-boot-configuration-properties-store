package com.ttulka.spring.boot.configstore;

import lombok.RequiredArgsConstructor;
import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.core.env.ConfigurableEnvironment;
import org.springframework.core.env.Environment;
import org.springframework.core.env.PropertySource;

import java.util.Map;

import static org.assertj.core.api.Assertions.assertThat;
import static org.junit.jupiter.api.Assertions.assertAll;

class ConfigurationStorePostProcessorTest {

    /**
     * Test implementation of {@link ConfigurationStorePostProcessor}
     */
    @RequiredArgsConstructor
    static class TestConfigurationStorePostProcessor extends ConfigurationStorePostProcessor {

        private final Map<String, Object> properties;

        @Override
        protected Map<String, Object> propertySource(Environment env) {
            return properties;
        }
    }

    @Nested
    @SpringBootTest(classes = ConfigurationStorePostProcessorTest.class)
    class JdbcStoreEnabledTest {

        private static final String PROP1 = "my.property1";
        private static final String PROP2 = "my.property2";
        private static final String VALUE1 = "value1";
        private static final String VALUE2 = "value2";

        @Autowired
        private ConfigurableEnvironment env;
        @MockBean
        private SpringApplication app;

        @Test
        void post_processing_loads_a_new_source_into_environment() {
            var postProcessor = new TestConfigurationStorePostProcessor(Map.of(PROP1, VALUE1, PROP2, VALUE2));
            postProcessor.postProcessEnvironment(env, app);

            assertThat(env.getPropertySources().contains(postProcessor.propertySourceName())).isTrue();
        }

        @Test
        void new_source_contains_properties() {
            var postProcessor = new TestConfigurationStorePostProcessor(Map.of(PROP1, VALUE1, PROP2, VALUE2));
            postProcessor.postProcessEnvironment(env, app);

            var source = env.getPropertySources().get(postProcessor.propertySourceName());

            assertAll(
                    () -> assertThat(source.containsProperty(PROP1)).isTrue(),
                    () -> assertThat(source.containsProperty(PROP1)).isTrue(),
                    () -> assertThat(source.getProperty(PROP1)).isEqualTo(VALUE1),
                    () -> assertThat(source.getProperty(PROP2)).isEqualTo(VALUE2)
            );
        }

        @Test
        void new_source_is_added_as_first() {
            var postProcessor = new TestConfigurationStorePostProcessor(Map.of(PROP1, VALUE1, PROP2, VALUE2));
            postProcessor.postProcessEnvironment(env, app);

            assertThat(env.getPropertySources().iterator().next().getName()).isEqualTo(postProcessor.propertySourceName());
        }
    }

    @Nested
    @SpringBootTest(classes = ConfigurationStorePostProcessorTest.class,
            properties = "spring.configstore.source.last=true")
    class JdbcStoreLastTest {

        @Autowired
        private ConfigurableEnvironment env;
        @MockBean
        private SpringApplication app;

        @Test
        void new_source_is_added_as_last() {
            var postProcessor = new TestConfigurationStorePostProcessor(Map.of());
            postProcessor.postProcessEnvironment(env, app);

            var it = env.getPropertySources().iterator();
            PropertySource<?> source = it.next();
            while (it.hasNext()) {
                source = it.next();
            }

            assertThat(source.getName()).isEqualTo(postProcessor.propertySourceName());
        }
    }
}
