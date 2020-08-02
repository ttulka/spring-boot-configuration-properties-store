package com.ttulka.spring.boot.configstore;

import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.context.properties.ConfigurationPropertiesBinding;
import org.springframework.boot.context.properties.ConversionServiceRetriever;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.context.ConfigurableApplicationContext;
import org.springframework.context.annotation.Bean;
import org.springframework.core.convert.converter.Converter;
import org.springframework.stereotype.Component;

import static org.assertj.core.api.Assertions.assertThat;

@SpringBootTest(classes = {ConverterTest.class, ConfigurationStoreAutoConfig.class, ConverterTest.MyObjectInvertedConverter.class})
class ConverterTest {

    @Autowired
    private ConfigurableApplicationContext applicationContext;

    @Test
    void custom_convertor_is_added() {
        var conversionService = new ConversionServiceRetriever(applicationContext).getConversionService();

        assertThat(conversionService.canConvert(MyObject.class, String.class)).isTrue();
    }

    @Component
    @ConfigurationPropertiesBinding
    static class MyObjectInvertedConverter implements Converter<MyObject, String> {

        @Override
        public String convert(MyObject from) {
            return from.toString();
        }
    }

    @Bean
    ConfigurationStoreEventListener defaultConfigurationStoreEventListener() {
        return (name, value) -> { /* do nothing */ };
    }

    static class MyObject {
    }
}
