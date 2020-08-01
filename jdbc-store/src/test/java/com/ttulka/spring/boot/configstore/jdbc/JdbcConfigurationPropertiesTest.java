package com.ttulka.spring.boot.configstore.jdbc;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.jdbc.JdbcTest;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.test.context.ContextConfiguration;

import java.util.Map;

import static org.assertj.core.api.Assertions.assertThat;

@JdbcTest
@ContextConfiguration(classes = JdbcConfigurationPropertiesTest.class)
class JdbcConfigurationPropertiesTest {

    private static final String TABLE = "test_properties";

    @Autowired
    private JdbcTemplate jdbcTemplate;

    @BeforeEach
    void initDatabase() {
        new InitDatabase(null, TABLE, jdbcTemplate).createStructures();
    }

    @Test
    void all_properties_are_found() {
        var update = new UpdateConfigurationProperty(TABLE, jdbcTemplate);
        var find = new FindConfigurationProperties(TABLE, jdbcTemplate);

        update.update("my.prop1", "test1");
        update.update("my.prop2", "test2");

        var props = find.all();

        assertThat(props).containsAllEntriesOf(Map.of(
                "my.prop1", "test1",
                "my.prop2", "test2"));
    }

    @Test
    void empty_properties_is_returned() {
        var find = new FindConfigurationProperties(TABLE, jdbcTemplate);

        var props = find.all();

        assertThat(props).isEmpty();
    }

    @Test
    void property_is_added() {
        var update = new UpdateConfigurationProperty(TABLE, jdbcTemplate);
        var find = new FindConfigurationProperties(TABLE, jdbcTemplate);

        update.update("my.prop1", "test1");

        var props = find.all();

        assertThat(props).containsEntry("my.prop1", "test1");
    }

    @Test
    void property_is_updated() {
        var update = new UpdateConfigurationProperty(TABLE, jdbcTemplate);
        var find = new FindConfigurationProperties(TABLE, jdbcTemplate);

        update.update("my.prop1", "test1");
        update.update("my.prop1", "test1-updated");

        var props = find.all();

        assertThat(props).containsEntry("my.prop1", "test1-updated");
    }
}
