package com.ttulka.spring.boot.configstore.jdbc;

import lombok.RequiredArgsConstructor;

import java.util.regex.Pattern;

@RequiredArgsConstructor
class SqlIdentifier {

    private final String identifier;

    public boolean isEmpty() {
        return identifier == null || identifier.isBlank();
    }

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
