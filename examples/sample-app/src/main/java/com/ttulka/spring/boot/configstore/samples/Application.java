package com.ttulka.spring.boot.configstore.samples;

import com.ttulka.spring.boot.configstore.ConfigurationStore;
import lombok.NonNull;
import lombok.RequiredArgsConstructor;
import lombok.Value;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.boot.context.properties.ConfigurationPropertiesBinding;
import org.springframework.boot.context.properties.EnableConfigurationProperties;
import org.springframework.core.convert.converter.Converter;
import org.springframework.scheduling.annotation.EnableAsync;
import org.springframework.stereotype.Component;

import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;

@SpringBootApplication
@EnableConfigurationProperties(SampleProperties.class)
@EnableAsync
public class Application {

    public static void main(String[] args) {
        SpringApplication.run(Application.class, args)
                .getBean(AppManagement.class)
                .doSomethingWithConfigurationProperties(); // check this out
    }

    @Component
    @RequiredArgsConstructor
    static class AppManagement {

        private final SampleProperties sampleProperties;
        private final ConfigurationStore configStore;

        void doSomethingWithConfigurationProperties() {
            int startupCounter = sampleProperties.getStartupCounter();

            System.out.println("STARTUP COUNTER: " + startupCounter);
            System.out.println("LAST STARTUP TIME: " + sampleProperties.getStartupTime());

            configStore.update("startup-counter", startupCounter + 1);
            configStore.update("startup-time", new MyDateTime(LocalDateTime.now()));
        }
    }

    @Component
    @ConfigurationPropertiesBinding
    class EmployeeConverter implements Converter<String, MyDateTime> {

        @Override
        public MyDateTime convert(String from) {
            return new MyDateTime(LocalDateTime.parse(from, DateTimeFormatter.ISO_DATE_TIME));
        }
    }

    @Component
    @ConfigurationPropertiesBinding
    class EmployeeInvertedConverter implements Converter<MyDateTime, String> {

        @Override
        public String convert(MyDateTime from) {
            return from.getDateTime().format(DateTimeFormatter.ISO_DATE_TIME);
        }
    }
}

@Value
class MyDateTime {
    private final @NonNull LocalDateTime dateTime;

    @Override
    public String toString() {
        return dateTime.format(DateTimeFormatter.ofPattern("YYYY-MM-dd HH:mm:ss"));
    }
}
