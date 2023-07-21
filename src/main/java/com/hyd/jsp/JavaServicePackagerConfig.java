package com.hyd.jsp;

import lombok.Data;
import org.springframework.boot.context.properties.ConfigurationProperties;

@Data
@ConfigurationProperties(prefix = "jsp")
public class JavaServicePackagerConfig {

  private String settingsFilePath = "java-service-packager.settings.json";
}
