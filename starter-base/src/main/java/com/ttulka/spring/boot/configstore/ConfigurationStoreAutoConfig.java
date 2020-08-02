package com.ttulka.spring.boot.configstore;

import lombok.RequiredArgsConstructor;
import org.springframework.boot.context.properties.ConversionServiceRetriever;
import org.springframework.context.ApplicationEventPublisher;
import org.springframework.context.ConfigurableApplicationContext;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.event.EventListener;
import org.springframework.core.convert.ConversionService;
import org.springframework.core.convert.converter.Converter;
import org.springframework.scheduling.annotation.Async;

/**
 * Base configuration of Configuration Store.
 *
 * Sets up a default event-based {@link ConfigurationStore}
 * and application-events-based {@link ConfigurationStoreEventPublisher}.
 *
 * Searchs for beans of type {@link Converter} to convert configuration properties into String.
 */
@Configuration
public class ConfigurationStoreAutoConfig {

    @Bean
    ConfigurationStore eventBasedConfigurationPropertiesStore(
            ConfigurableApplicationContext applicationContext,
            ConfigurationStoreEventPublisher configurationStoreEventPublisher) {
        var conversionService = new ConversionServiceRetriever(applicationContext).getConversionService();
        return (name, value) -> configurationStoreEventPublisher.raise(
                    new ConfigurationPropertyUpdated(name, convertedToString(value, conversionService)));
    }

    @Bean
    ConfigurationStoreEventPublisher applicationConfigurationStoreEventPublisher(ApplicationEventPublisher publisher) {
        return publisher::publishEvent;
    }

    @Configuration
    static class ConfigurationPropertyUpdatedEventListenerConfig {

        @Bean
        ConfigurationPropertyUpdatedEventListener configurationPropertyUpdatedEventListener(ConfigurationStoreEventListener configurationStoreEventListener) {
            return new ConfigurationPropertyUpdatedEventListener(configurationStoreEventListener);
        }

        @RequiredArgsConstructor
        static class ConfigurationPropertyUpdatedEventListener {

            private final ConfigurationStoreEventListener configurationStoreEventListener;

            @Async
            @EventListener
            void handleEvent(ConfigurationPropertyUpdated event) {
                configurationStoreEventListener.onUpdated(event.getName(), event.getValue());
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