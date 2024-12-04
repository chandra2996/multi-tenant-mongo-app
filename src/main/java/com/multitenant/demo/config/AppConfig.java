package com.multitenant.demo.config;


import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.context.annotation.Configuration;

import java.util.Map;

@Configuration
@ConfigurationProperties(prefix = "app")
@Getter
@Setter
@AllArgsConstructor
@NoArgsConstructor
public class AppConfig {


    private Map<String, String> connStrings;
}
