package com.ttulka.spring.boot.configstore.jdbc;

import lombok.Getter;
import lombok.Setter;
import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.util.StringUtils;

@ConfigurationProperties("spring.configstore.jdbc")
@Getter
@Setter
class JdbcConfigStoreConfigurationProperties {

    static final String TABLE_DEFAULT = "configstore_properties";

    private String schema;
    private String table = TABLE_DEFAULT;

    public String tableName() {
        return StringUtils.hasLength(schema) ? schema + "." + table : table;
    }
}
