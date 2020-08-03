package com.ttulka.spring.boot.configstore;

import lombok.Getter;
import lombok.Setter;
import org.springframework.boot.context.properties.ConfigurationProperties;

@ConfigurationProperties("spring.configstore")
@Getter
@Setter
class ConfigurationStoreProperties {

    private String prefix;
}
