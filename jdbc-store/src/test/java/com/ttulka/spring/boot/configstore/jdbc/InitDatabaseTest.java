package com.ttulka.spring.boot.configstore.jdbc;

import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.jdbc.JdbcTest;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.test.context.ContextConfiguration;

import static org.assertj.core.api.Assertions.assertThat;

@JdbcTest
@ContextConfiguration(classes = InitDatabaseTest.class)
class InitDatabaseTest {

    @Autowired
    private JdbcTemplate jdbcTemplate;

    @Test
    void database_initialized_with_schema() {
        new InitDatabase("testschema", "testtable", jdbcTemplate)
                .createStructures();

        var res = jdbcTemplate.queryForObject("SELECT COUNT(*) FROM testschema.testtable", Integer.class);

        assertThat(res).isNotNull();
    }

    @Test
    void database_initialized_without_schema() {
        new InitDatabase(null, "testtable", jdbcTemplate)
                .createStructures();

        var res = jdbcTemplate.queryForObject("SELECT COUNT(*) FROM testtable", Integer.class);

        assertThat(res).isNotNull();
    }
}
