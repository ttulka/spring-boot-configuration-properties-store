# Configuration Properties Store for Spring Boot

Mutable persistent configuration properties that survive an application restart.

Extendable store types. JDBC store as default. 

## Usage

### Maven dependency

```
<dependency>
    <groupId>com.ttulka.spring.boot.configstore</groupId>
    <artifactId>configuration-properties-store-jdbc-spring-boot-starter</artifactId>
    <version>1.3.1</version>
</dependency>
```

### Application setting

The application environment must contain data source configuration properties.

For example in `application.yml`: 

```yaml
spring:
  datasource:
    url: jdbc:h2:./mydb
    driverClassName: org.h2.Driver
    username: sa
    password:
```

### Update a configuration property

```java
@Autowired
ConfigurationStore configStore;
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

#### Asynchronous update

Processing of the property update is done via application events, these are synchronous by default.

You can enable running async mode by annotation one of your configurations with `@EnableAsync`.

## Settings

- `spring.configstore.prefix` (default: `null`) 
  - Prefix added to a property name: `<prefix>.<propName>`.
  - When `null`, no prefix is added.
  - String value.
- `spring.configstore.source.last` (default: `false`)
  - The property source is added as last, otherwise as first. 
  - Boolean value.
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
  
### Custom converters

As well as for configuration properties, custom converters can be added. To each custom converter there must be an inverted converter:

```java
@Component
@ConfigurationPropertiesBinding
class EmployeeConverter implements Converter<String, Employee> {
 
    @Override
    public Employee convert(String from) {
        String[] data = from.split(",");
        return new Employee(data[0], Double.parseDouble(data[1]));
    }
}

@Component
@ConfigurationPropertiesBinding
class EmployeeInvertedConverter implements Converter<Employee, String> {
 
    @Override
    public String convert(Employee from) {
        return from.firstName() + "," + from.lastName();
    }
}
```

Then, the class could be used in configuration properties as well as in configuration store:

```java
@ConfigurationProperties
public class SampleProperties {

    private Employee employee;
    ...
}
...
configStore.update("employee", new Employee("Homer", "Simpson"));
```
  
## Customizing

There are several interfaces to be implemented for customizing the store.

- `ConfigurationPropertiesStore`
  - The default implementation publishes an event.
  - The entry point, should be autowired in the application.
  
      ```java
      @Autowired
      ConfigurationStore configStore;
    
      configStore.update("my-prop", 123);
      ```
    
- `ConfigurationStoreEventPublisher`
  - The default implementation uses Spring's `ApplicationEventPublisher`.
  - Could be extended together with an event handling that calls `ConfigurationStoreEventListener`:
    
    ```java
    @Bean
    ConfigurationStoreEventPublisher applicationConfigurationStoreEventPublisher(
            ApplicationEventPublisher publisher) {
        return publisher::publishEvent;
    }
    ```
    
    And:
      
    ```java
    @Component
    @RequiredArgsConstructor
    class ConfigurationPropertyUpdatedEventListener {

        private final ConfigurationStoreEventListener configurationStoreEventListener;

        @EventListener
        void handleEvent(ConfigurationPropertyUpdated event) {
            configurationStoreEventListener.on(event);
        }
    }
    ```
    
## Building

```
mvn clean install
```
    
## Extending 

So far there is the following implementation:

- JDBC
    - `com.ttulka.spring.boot.configstore:configuration-properties-store-jdbc-spring-boot-starter`

Then implementing a new extension, at least `ConfigurationStoreEventListener` must be implemented.

Next, a new implementation of `ConfigurationStorePostProcessor` is needed to load properties into the environment. 
