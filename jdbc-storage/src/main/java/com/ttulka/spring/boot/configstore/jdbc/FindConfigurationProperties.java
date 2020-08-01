package com.ttulka.spring.boot.configstore.jdbc;

import lombok.NonNull;
import lombok.RequiredArgsConstructor;
import org.springframework.jdbc.core.JdbcTemplate;

import java.util.Map;

import static java.util.stream.Collectors.toMap;

@RequiredArgsConstructor
class FindConfigurationProperties {

    private final @NonNull String table;

    private final JdbcTemplate jdbcTemplate;

    public Map<String, Object> all() {
        return jdbcTemplate.queryForList(
                "SELECT name, value  FROM " + new SqlIdentifier(table)).stream()
                .collect(toMap(rs -> (String) rs.get("name"), rs -> rs.get("value")));
    }
}
