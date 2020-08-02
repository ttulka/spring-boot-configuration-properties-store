package com.ttulka.spring.boot.configstore;

/**
 * Event publisher to implemented by a particular infrastructure.
 */
public interface ConfigurationStoreEventPublisher {

    /**
     * Raises an event.
     * @param propertyUpdated the event
     */
    void raise(ConfigurationPropertyUpdated propertyUpdated);
}
