package com.ttulka.spring.boot.configstore.samples;

import lombok.Getter;
import lombok.Setter;
import org.springframework.boot.context.properties.ConfigurationProperties;

import static com.ttulka.spring.boot.configstore.samples.SampleProperties.PREFIX;

@ConfigurationProperties(PREFIX)
@Getter
@Setter
public class SampleProperties {

    public static final String PREFIX = "my.app";

    private int startupCounter;

    private MyDateTime startupTime;
}
