package com.ttulka.spring.boot.configstore;

import lombok.RequiredArgsConstructor;
import org.springframework.boot.context.properties.ConversionServiceRetriever;
import org.springframework.context.ApplicationEventPublisher;
import org.springframework.context.ConfigurableApplicationContext;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.event.EventListener;
import org.springframework.core.convert.ConversionService;

@Configuration
public class ConfigurationStoreAutoConfig {

    @Bean
    ConfigurationStore eventBasedConfigurationPropertiesStore(
            ConfigurableApplicationContext applicationContext,
            ConfigurationStoreEventPublisher configurationStoreEventPublisher) {
        var conversionService = new ConversionServiceRetriever(applicationContext).getConversionService();
        return (name, value) -> configurationStoreEventPublisher.raise(
                new ConfigurationPropertyChanged(name, convertedToString(value, conversionService)));
    }

    @Bean
    ConfigurationStoreEventPublisher applicationConfigurationStoreEventPublisher(ApplicationEventPublisher publisher) {
        return publisher::publishEvent;
    }

    @Configuration
    static class ConfigurationPropertyChangedEventListenerConfig {

        @Bean
        ConfigurationPropertyChangedEventListener configurationPropertyChangedEventListener(ConfigurationStoreEventListener configurationStoreEventListener) {
            return new ConfigurationPropertyChangedEventListener(configurationStoreEventListener);
        }

        @RequiredArgsConstructor
        static class ConfigurationPropertyChangedEventListener {

            private final ConfigurationStoreEventListener configurationStoreEventListener;

            @EventListener
            void handleEvent(ConfigurationPropertyChanged event) {
                configurationStoreEventListener.onChanged(event.getName(), event.getValue());
            }
        }

//        @ConditionalOnMissingBean(ConfigurationStoreEventListener.class)
//        @Configuration
//        static class DefaultConfigurationStoreEventListenerConfig {
//            @Bean
//            ConfigurationStoreEventListener defaultConfigurationStoreEventListener() {
//                return (name, value) -> { throw new UnsupportedOperationException("ConfigurationStoreEventListener not implemented!"); };
//            }
//        }
    }

    private String convertedToString(Object o, ConversionService conversionService) {
        if (o == null) {
            return null;
        }
        if (conversionService.canConvert(o.getClass(), String.class)) {
            return conversionService.convert(o, String.class);
        }
        return o.toString();
    }
}