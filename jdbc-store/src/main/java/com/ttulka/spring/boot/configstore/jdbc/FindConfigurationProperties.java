package com.ttulka.spring.boot.configstore.jdbc;

import lombok.NonNull;
import lombok.RequiredArgsConstructor;
import org.springframework.jdbc.core.JdbcTemplate;

import java.util.Map;

import static java.util.stream.Collectors.toMap;

/**
 * Finding configuration properties from a JDBC source.
 */
@RequiredArgsConstructor
public class FindConfigurationProperties {

    private final @NonNull String table;

    private final JdbcTemplate jdbcTemplate;

    /**
     * Finds all configuration properties.
     * @return a map of configuration properties.
     */
    public Map<String, Object> all() {
        return jdbcTemplate.queryForList(
                "SELECT prop_name, prop_value  FROM " + new SqlIdentifier(table)).stream()
                .collect(toMap(rs -> (String) rs.get("prop_name"), rs -> rs.get("prop_value")));
    }
}
