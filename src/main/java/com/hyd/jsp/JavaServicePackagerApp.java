package com.hyd.jsp;

import io.swagger.v3.oas.annotations.OpenAPIDefinition;
import io.swagger.v3.oas.annotations.info.Info;
import io.swagger.v3.oas.annotations.servers.Server;
import io.swagger.v3.oas.annotations.tags.Tag;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.boot.context.properties.EnableConfigurationProperties;

@SpringBootApplication
@OpenAPIDefinition(
  info = @Info(title = "Java 项目构建服务"),
  servers = {
    @Server(url = "http://localhost:8080", description = "开发环境"),
    @Server(url = "http://localhost:8081", description = "测试环境")
  },
  tags = {
    @Tag(name = "构建配置接口", description = "用于存取构建配置的接口")
  }
)
@EnableConfigurationProperties({
  JavaServicePackagerConfig.class
})
public class JavaServicePackagerApp {

  public static void main(String[] args) {
    SpringApplication.run(JavaServicePackagerApp.class, args);
  }
}
