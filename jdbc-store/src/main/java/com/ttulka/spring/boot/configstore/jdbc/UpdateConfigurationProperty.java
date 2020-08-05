package com.ttulka.spring.boot.configstore.jdbc;

import lombok.NonNull;
import lombok.RequiredArgsConstructor;
import org.springframework.jdbc.core.JdbcTemplate;

/**
 * Updating of a configuration property.
 */
@RequiredArgsConstructor
class UpdateConfigurationProperty {

    private final @NonNull String table;

    private final @NonNull JdbcTemplate jdbcTemplate;

    /**
     * Updates a configuration property.
     * @param name the property name
     * @param value the property value
     */
    public void update(@NonNull String name, Object value) {
        if (value == null) {
            deleteProperty(name);
            return;
        }
        if (hasProperty(name)) {
            updateProperty(name, value);
        } else {
            insertProperty(name, value);
        }
    }

    private void insertProperty(String name, Object value) {
        jdbcTemplate.update("INSERT INTO " + new SqlIdentifier(table) + " VALUES(?, ?)",
                name, asString(value));
    }

    private void updateProperty(String name, Object value) {
        jdbcTemplate.update("UPDATE " + new SqlIdentifier(table) + " SET value = ? WHERE name = ?",
                asString(value), name);
    }

    private void deleteProperty(String name) {
        jdbcTemplate.update("DELETE FROM " + new SqlIdentifier(table) + " WHERE name = ?",
                name);
    }

    private boolean hasProperty(String name) {
        return jdbcTemplate.queryForObject("SELECT COUNT(*) FROM " + new SqlIdentifier(table) + " WHERE name = ?",
                Integer.class, name) > 0;
    }

    private String asString(Object o) {
        return o != null ? o.toString() : null;
    }
}
