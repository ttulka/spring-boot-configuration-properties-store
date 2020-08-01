package com.ttulka.spring.boot.configstore.jdbc;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import javax.annotation.PostConstruct;

@Component
class InitDatabaseStarter {

    @Autowired(required = false)
    private InitDatabase initDatabase;

    @PostConstruct
    void init() {
        if (initDatabase != null) {
            initDatabase.createStructures();
        }
    }
}
