package com.studyolle.infra.config;

import lombok.Data;
import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.stereotype.Component;
@ConfigurationProperties("app")
@Data
@Component
public class AppProperties {
    private String host;
}
