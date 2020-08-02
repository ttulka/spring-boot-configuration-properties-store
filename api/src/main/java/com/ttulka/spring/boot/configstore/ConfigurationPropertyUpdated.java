package com.ttulka.spring.boot.configstore;

import lombok.Value;

/**
 * DTO for application event.
 */
@Value
public final class ConfigurationPropertyUpdated {

    private final String name;
    private final String value;
}
