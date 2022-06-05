package com.ttulka.spring.boot.configstore.jdbc;

import lombok.NonNull;
import lombok.RequiredArgsConstructor;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.transaction.annotation.Transactional;

/**
 * Updating of a configuration property.
 */
@RequiredArgsConstructor
@Transactional
public class UpdateConfigurationProperty {

    private final @NonNull String table;

    private final @NonNull JdbcTemplate jdbcTemplate;

    /**
     * Updates a configuration property.
     * @param name the property name
     * @param value the property value
     */
    public void update(@NonNull String name, String value) {
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

    private void insertProperty(String name, String value) {
        jdbcTemplate.update("INSERT INTO " + new SqlIdentifier(table) + " VALUES(?, ?)",
                name, value);
    }

    private void updateProperty(String name, String value) {
        jdbcTemplate.update("UPDATE " + new SqlIdentifier(table) + " SET prop_value = ? WHERE prop_name = ?",
                value, name);
    }

    private void deleteProperty(String name) {
        jdbcTemplate.update("DELETE FROM " + new SqlIdentifier(table) + " WHERE prop_name = ?",
                name);
    }

    private boolean hasProperty(String name) {
        return jdbcTemplate.queryForObject("SELECT COUNT(*) FROM " + new SqlIdentifier(table) + " WHERE prop_name = ?",
                Integer.class, name) > 0;
    }
}
