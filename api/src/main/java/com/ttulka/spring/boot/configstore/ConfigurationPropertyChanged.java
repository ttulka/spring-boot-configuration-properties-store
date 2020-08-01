package com.ttulka.spring.boot.configstore;

import lombok.Value;

@Value
public final class ConfigurationPropertyChanged {

    private final String name;
    private final String value;
}
