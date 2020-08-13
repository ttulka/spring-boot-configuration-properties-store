package com.ttulka.spring.boot.configstore;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.env.EnvironmentPostProcessor;
import org.springframework.core.Ordered;
import org.springframework.core.env.*;

import java.util.Map;

/**
 * To be implemented for a concrete store.
 */
public abstract class ConfigurationStorePostProcessor implements EnvironmentPostProcessor, Ordered {

    private static final String PROPERTY_SOURCE_NAME = "propertiesFromConfigurationStore";

    /**
     * Creates a new property source for a particular store to be added into the environment.
     * To be implemented by custom stores.
     *
     * @param environment the actual environment
     * @return the property source
     */
    protected abstract Map<String, Object> propertySource(Environment environment);

    @Override
    public final void postProcessEnvironment(ConfigurableEnvironment env, SpringApplication application) {
        try {
            var newSource = new MapPropertySource(propertySourceName(), propertySource(env));

            if (env.getProperty("spring.configstore.source.last", "false").equalsIgnoreCase("true")) {
                addAsLast(env.getPropertySources(), newSource);
            } else {
                addAsFirst(env.getPropertySources(), newSource);
            }

        } catch (Exception e) {
            throw new RuntimeException(e);
        }
    }

    public String propertySourceName() {
        return PROPERTY_SOURCE_NAME;
    }

    @Override
    public int getOrder() {
        return Ordered.LOWEST_PRECEDENCE;
    }

    private void addAsFirst(MutablePropertySources sources, PropertySource<?> propertySource) {
        sources.addFirst(propertySource);
    }

    private void addAsLast(MutablePropertySources sources, PropertySource<?> propertySource) {
        sources.addLast(propertySource);
    }
}
