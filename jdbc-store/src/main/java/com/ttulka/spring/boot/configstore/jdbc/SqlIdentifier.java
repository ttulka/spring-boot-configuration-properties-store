package com.ttulka.spring.boot.configstore.jdbc;

import lombok.RequiredArgsConstructor;

import java.util.regex.Pattern;

/**
 * SQL identifier for a schema or table name.
 */
@RequiredArgsConstructor
public class SqlIdentifier {

    private final String identifier;

    /**
     * Checks the identifier for emptiness.
     * @return true if the identifier is empty, otherwise false
     */
    public boolean isEmpty() {
        return identifier == null || identifier.isBlank();
    }

    /**
     * Returns the identifier as String.
     * @return the identifier as String
     */
    @Override
    public String toString() {
        var identifier = this.identifier.strip();
        validate(identifier);
        return identifier;
    }

    private void validate(String sqlIdentifier) {
        if (!Pattern.matches("[a-zA-z_][a-zA-z0-9_.]*", sqlIdentifier)) {
            throw new IllegalArgumentException("Invalid SQL identifier: " + sqlIdentifier);
        }
    }
}
