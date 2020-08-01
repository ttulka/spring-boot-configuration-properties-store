package com.ttulka.spring.boot.configstore.samples;

import com.ttulka.spring.boot.configstore.ConfigurationStore;
import lombok.RequiredArgsConstructor;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.boot.context.properties.EnableConfigurationProperties;
import org.springframework.context.ApplicationContext;
import org.springframework.scheduling.annotation.EnableAsync;
import org.springframework.stereotype.Component;

@SpringBootApplication
@EnableConfigurationProperties(SampleProperties.class)
@EnableAsync
public class Application {

    public static void main(String[] args) {
        ApplicationContext ac = SpringApplication.run(Application.class, args);

        AppManagement appManagement = ac.getBean(AppManagement.class);

        int startupCounter = appManagement.startupCounter();

        System.out.println("STARTUP COUNTER: " + startupCounter);

        appManagement.updateLastStartup(startupCounter + 1);
    }

    @Component
    @RequiredArgsConstructor
    static class AppManagement {

        private final SampleProperties sampleProperties;
        private final MyAppConfigurationStore configStore;

        Integer startupCounter() {
            return sampleProperties.getStartupCounter();
        }

        void updateLastStartup(int counter) {
            configStore.update("startup-counter", counter);
        }
    }

    /**
     * Application decorator for ConfigurationStore
     */
    @Component
    @RequiredArgsConstructor
    static class MyAppConfigurationStore implements ConfigurationStore {

        private final ConfigurationStore configStore;

        @Override
        public void update(String propName, Object propValue) {
            configStore.update(SampleProperties.PREFIX + "." + propName, propValue);
        }
    }
}
