package com.ttulka.spring.boot.configstore;

public interface ConfigurationStoreEventPublisher {

    void raise(ConfigurationPropertyChanged propertyChanged);
}
