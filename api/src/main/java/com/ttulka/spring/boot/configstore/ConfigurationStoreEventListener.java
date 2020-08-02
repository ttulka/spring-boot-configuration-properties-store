package com.ttulka.spring.boot.configstore;

/**
 * Event listener to be implemented of a particular store.
 */
public interface ConfigurationStoreEventListener {

    /**
     * Is invoked on a event.
     * @param name the property name
     * @param value the updated value
     */
    void onUpdated(String name, String value);
}
