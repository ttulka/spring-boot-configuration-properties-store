package com.ttulka.spring.boot.configstore.samples;

import lombok.Getter;
import lombok.Setter;
import org.springframework.boot.context.properties.ConfigurationProperties;

@ConfigurationProperties("my.app")
@Getter
@Setter
public class SampleProperties {

    private int startupCounter;

    private MyDateTime startupTime;
}
