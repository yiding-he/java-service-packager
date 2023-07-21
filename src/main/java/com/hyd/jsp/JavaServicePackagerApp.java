package com.hyd.jsp;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.boot.context.properties.EnableConfigurationProperties;

@SpringBootApplication
@EnableConfigurationProperties({
  JavaServicePackagerConfig.class
})
public class JavaServicePackagerApp {

  public static void main(String[] args) {
    SpringApplication.run(JavaServicePackagerApp.class, args);
  }
}
