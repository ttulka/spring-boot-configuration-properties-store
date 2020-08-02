package com.ttulka.spring.boot.configstore;

/**
 * Entry point to the Configuration Store.
 */
public interface ConfigurationStore {

    /**
     * Updates a property.
     * @param propName the property name
     * @param propValue the updated value
     */
    void update(String propName, Object propValue);
}
