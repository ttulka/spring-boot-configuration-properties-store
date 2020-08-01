package com.ttulka.spring.boot.configstore;

public interface ConfigurationStoreEventListener {

    void onChanged(String name, String value);
}
