package com.hyd.jsp.settings;

import com.hyd.jsp.JavaServicePackagerConfig;
import com.hyd.jsp.utils.Jackson;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import javax.annotation.PostConstruct;

@Component
public class SettingsRepository {

  @Autowired
  private JavaServicePackagerConfig config;

  @PostConstruct
  private void init() {

  }
}
