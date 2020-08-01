package com.ttulka.spring.boot.configstore.jdbc;

import lombok.NonNull;
import lombok.RequiredArgsConstructor;
import org.springframework.jdbc.core.JdbcTemplate;

@RequiredArgsConstructor
class InitDatabase {

    private final String schema;
    private final @NonNull String table;

    private final @NonNull JdbcTemplate jdbcTemplate;

    public void createStructures() {
        SqlIdentifier schemaName = new SqlIdentifier(schema);
        SqlIdentifier tableName = new SqlIdentifier(table);

        if (!schemaName.isEmpty()) {
            createSchema(schemaName);
            tableName = new SqlIdentifier(schemaName + "." + tableName);
        }

        if (tableName.isEmpty()) {
            throw new IllegalArgumentException("Table name must not be empty.");
        }

        createTable(tableName);
    }

    private void createSchema(SqlIdentifier schemaName) {
        jdbcTemplate.update("CREATE SCHEMA IF NOT EXISTS " + schemaName);
    }

    private void createTable(SqlIdentifier tableName) {
        jdbcTemplate.update("CREATE TABLE IF NOT EXISTS " + tableName +
                " (name VARCHAR(255) PRIMARY KEY, value VARCHAR(255))");
    }
}