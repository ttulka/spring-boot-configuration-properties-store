package com.ttulka.spring.boot.configstore;

import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.context.properties.ConfigurationPropertiesBinding;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.context.annotation.Configuration;
import org.springframework.core.convert.converter.Converter;
import org.springframework.stereotype.Component;

import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.Mockito.verify;

public class DefaultConfigurationStoreTest {

    @Nested
    @SpringBootTest(classes = {ConfigurationStoreAutoConfig.class,
            DefaultConfigurationStoreTest.PublisherListenerTestConfig.class}, properties = {
            "spring.configstore.prefix=my.test"})
    class WithPrefixTest {

        @Autowired
        private ConfigurationStore configStore;
        @Autowired
        private ConfigurationStoreEventPublisher mockPublisher;

        @Test
        void prefix_is_added_to_the_property_name() {
            configStore.update("my-prop", "test123");

            verify(mockPublisher).raise(eq(new ConfigurationPropertyUpdated(
                    "my.test.my-prop", "test123")));
        }
    }

    @Nested
    @SpringBootTest(classes = {ConfigurationStoreAutoConfig.class,
            DefaultConfigurationStoreTest.PublisherListenerTestConfig.class,
            DefaultConfigurationStoreTest.CustomConvertersTestConfig.class})
    class WithCustomConverterTest {

        @Autowired
        private ConfigurationStore configStore;
        @Autowired
        private ConfigurationStoreEventPublisher mockPublisher;

        @Test
        void custom_object_value_is_converted() {
            configStore.update("my-prop", new MyClass());

            verify(mockPublisher).raise(eq(new ConfigurationPropertyUpdated(
                    "my-prop", MyClass.class.getName())));
        }
    }

    @Configuration
    static class PublisherListenerTestConfig {
        @MockBean
        private ConfigurationStoreEventPublisher mockPublisher;
        @MockBean
        private ConfigurationStoreEventListener mockListener;
    }

    @Configuration
    static class CustomConvertersTestConfig {

        @ConfigurationPropertiesBinding
        @Component
        static class MyClassConverter implements Converter<String, MyClass> {
            @Override
            public MyClass convert(String s) {
                return new MyClass();
            }
        }

        @ConfigurationPropertiesBinding
        @Component
        static class MyClassInvertedConverter implements Converter<MyClass, String> {
            @Override
            public String convert(MyClass myClass) {
                return MyClass.class.getName();
            }
        }
    }

    static class MyClass {}
}
