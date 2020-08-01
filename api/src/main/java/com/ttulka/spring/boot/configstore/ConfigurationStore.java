package com.ttulka.spring.boot.configstore;

public interface ConfigurationStore {

    void update(String propName, Object propValue);
}
