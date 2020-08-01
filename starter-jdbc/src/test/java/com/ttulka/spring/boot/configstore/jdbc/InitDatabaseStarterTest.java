package com.ttulka.spring.boot.configstore.jdbc;

import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.Import;
import org.springframework.test.context.junit.jupiter.SpringExtension;

import static org.mockito.Mockito.verify;

@ExtendWith(SpringExtension.class)
@ExtendWith(MockitoExtension.class)
class InitDatabaseStarterTest {

    @MockBean
    private InitDatabase initDatabase;

    @Test
    void database_init_stars() {
        verify(initDatabase).createStructures();
    }

    @Import(InitDatabaseStarter.class)
    @Configuration
    static class InitDatabaseStarterTestConfig {
    }
}
