# Configuration Properties Store for Spring Boot

Mutable persistent configuration properties that survive an application restart.

## Usage

### Maven dependency

```
<dependency>
    <groupId>com.ttulka.spring.boot.configstore</groupId>
    <artifactId>configuration-properties-store-jdbc-spring-boot-starter</artifactId>
    <version>1.0.0-SNAPSHOT</version>
</dependency>
```

### Update a configuration property

```java
@Autowired
ConfigurationPropertiesStore configStore;
...
configStore.update("my-property", "value123");
```

Will be persisted and loaded at the next application startup:

```java
@ConfigurationProperties
@Getter
@Setter
public class SampleProperties {

    private String myProperty;
}
```

## Settings

- `spring.configstore.source.last` (default: `false`)
  - The property source is added as last, otherwise as first. 
- `spring.configstore.jdbc.enabled` (default: `true`)
  - JDBC-based store is enabled/disabled.
  - Boolean value. 
- `spring.configstore.jdbc.schema` (default: `null`)
  - Schema for the property store database table.
  - When not set (`null`) will use the default schema.
  - String value.
- `spring.configstore.jdbc.table` (default: `configstore_properties`)
  - Database table name for the  property store.
  - When a schema set will result into `<schema>.<table>`.
  - String value.
  
## Customizing

There are several interfaces to be implemented for customizing the store.

- `ConfigurationPropertiesStore`
  - The default implementation publishes an event.
  - The entry point, could be extended via a decorator:
  
      ```java
      @RequiredArgsConstructor
      class MyAppConfigurationStore implements ConfigurationPropertiesStore {
  
          private final ConfigurationStore configStore;
  
          @Override
          public void update(String propName, Object propValue) {
              configStore.update("my.app." + propName, propValue);
          }
      }
      ```
- `ConfigurationStoreEventPublisher`
  - The default implementation uses Spring's `ApplicationEventPublisher`.
  - Could be extended together with an event handling that calls `ConfigurationStoreEventListener`:
    
    ```java
    @Bean
    ConfigurationStoreEventPublisher applicationConfigurationStoreEventPublisher(ApplicationEventPublisher publisher) {
        return publisher::publishEvent;
    }
    ```
    
    And:
      
    ```java
    @Component
    @RequiredArgsConstructor
    class ConfigurationPropertyChangedEventListener {

        private final ConfigurationStoreEventListener configurationStoreEventListener;

        @EventListener
        void handleEvent(ConfigurationPropertyChanged event) {
            configurationStoreEventListener.on(event);
        }
    }
    ```
    
## Extending 

So far there is the following implementation:

- JDBC
    - `com.ttulka.spring.boot.configstore:configuration-properties-store-jdbc-spring-boot-starter`

Then implementing a new extension, at least `ConfigurationStoreEventListener` must be implemented.

Next, a new implementation of `EnvironmentPostProcessor` is needed to load properties into the environment. 