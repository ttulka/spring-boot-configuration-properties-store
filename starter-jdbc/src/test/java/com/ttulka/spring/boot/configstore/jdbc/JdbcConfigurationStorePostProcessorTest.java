package com.ttulka.spring.boot.configstore.jdbc;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.jdbc.DataSourceBuilder;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.core.env.ConfigurableEnvironment;
import org.springframework.jdbc.core.JdbcTemplate;

import static org.assertj.core.api.Assertions.assertThat;
import static org.junit.jupiter.api.Assertions.assertAll;

class JdbcConfigurationStorePostProcessorTest {

    @Nested
    @SpringBootTest(classes = JdbcConfigurationStorePostProcessorTest.class, properties = {
            "spring.datasource.url=jdbc:h2:mem:testdb",
            "spring.datasource.driverClassName=org.h2.Driver",
            "spring.datasource.username=sa",
            "spring.datasource.password=",
            "spring.configstore.jdbc.schema=testschema",
            "spring.configstore.jdbc.table=testtable"})
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
            var postProcessor = new JdbcConfigurationStorePostProcessor();
            postProcessor.postProcessEnvironment(env, app);

            assertThat(env.getPropertySources().contains(postProcessor.propertySourceName())).isTrue();
        }

        @Test
        void new_source_contains_properties() {
            var postProcessor = new JdbcConfigurationStorePostProcessor();
            postProcessor.postProcessEnvironment(env, app);

            var source = env.getPropertySources().get(postProcessor.propertySourceName());

            assertAll(
                    () -> assertThat(source.containsProperty(PROP1)).isTrue(),
                    () -> assertThat(source.containsProperty(PROP1)).isTrue(),
                    () -> assertThat(source.getProperty(PROP1)).isEqualTo(VALUE1),
                    () -> assertThat(source.getProperty(PROP2)).isEqualTo(VALUE2)
            );
        }

        @BeforeEach
        void initDatabase() {
            var jdbTemplate = new JdbcTemplate(DataSourceBuilder.create()
                    .url(env.getProperty("spring.datasource.url"))
                    .username(env.getProperty("spring.datasource.username"))
                    .password(env.getProperty("spring.datasource.password"))
                    .driverClassName(env.getProperty("spring.datasource.driver-class-name"))
                    .build());
            new InitDatabase("testschema", "testtable", jdbTemplate)
                    .createStructures();

            var update = new UpdateConfigurationProperty("testschema.testtable", jdbTemplate);
            update.update(PROP1, VALUE1);
            update.update(PROP2, VALUE2);
        }
    }

    @Nested
    @SpringBootTest(classes = JdbcConfigurationStorePostProcessorTest.class, properties = {
            "spring.configstore.jdbc.enabled=false",
            "spring.datasource.url=jdbc:h2:mem:testdb",
            "spring.datasource.driverClassName=org.h2.Driver",
            "spring.datasource.username=sa",
            "spring.datasource.password="})
    class JdbcStoreDisabledTest {

        private static final String PROP1 = "my.property1";
        private static final String VALUE1 = "value1";

        @Autowired
        private ConfigurableEnvironment env;
        @MockBean
        private SpringApplication app;

        @Test
        void property_source_is_empty() {
            var postProcessor = new JdbcConfigurationStorePostProcessor();
            postProcessor.postProcessEnvironment(env, app);

            assertThat(env.getPropertySources().get(postProcessor.propertySourceName()).getProperty(PROP1)).isNull();
        }

        @BeforeEach
        void initDatabase() {
            var jdbTemplate = new JdbcTemplate(DataSourceBuilder.create()
                    .url(env.getProperty("spring.datasource.url"))
                    .username(env.getProperty("spring.datasource.username"))
                    .password(env.getProperty("spring.datasource.password"))
                    .driverClassName(env.getProperty("spring.datasource.driver-class-name"))
                    .build());
            new InitDatabase("testschema", "testtable", jdbTemplate)
                    .createStructures();

            var update = new UpdateConfigurationProperty("testschema.testtable", jdbTemplate);
            update.update(PROP1, VALUE1);
        }
    }

    @Nested
    @SpringBootTest(classes = JdbcConfigurationStorePostProcessorTest.class, properties = {
            "spring.datasource.url=jdbc:h2:mem:testdb",
            "spring.datasource.driverClassName=org.h2.Driver",
            "spring.datasource.username=sa",
            "spring.datasource.password="})
    class JdbcStoreDatabaseNotInitializedTest {

        @Autowired
        private ConfigurableEnvironment env;
        @MockBean
        private SpringApplication app;

        @Test
        void post_processing_does_nothing() {
            new JdbcConfigurationStorePostProcessor().postProcessEnvironment(env, app);
        }
    }
}
