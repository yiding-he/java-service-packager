package com.hyd.jsp.settings;

import com.hyd.jsp.JavaServicePackagerConfig;
import com.hyd.jsp.model.PackagingParams;
import com.hyd.jsp.model.ProjectSettings;
import com.hyd.jsp.model.Settings;
import com.hyd.jsp.utils.Jackson;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import javax.annotation.PostConstruct;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;

@Component
@Slf4j
public class SettingsRepository {

  @Autowired
  private JavaServicePackagerConfig config;

  private Settings settings;

  @PostConstruct
  private void init() throws IOException {
    var settingsFile = Path.of(config.getSettingsFilePath());
    if (Files.exists(settingsFile)) {
      var json = Files.readString(settingsFile);
      this.settings = Jackson.deserializeStandardJson(json, Settings.class);
      log.info("Settings loaded from file {}.", config.getSettingsFilePath());
    } else {
      this.settings = new Settings();
      log.info("Settings file {} not found. Using default settings.", config.getSettingsFilePath());
    }
  }

  public Settings getSettings() {
    return settings;
  }

  public void saveSettings() throws IOException {
    var json = Jackson.serializeStandardJson(settings);
    Files.writeString(Path.of(config.getSettingsFilePath()), json);
    log.info("Settings saved to file {}.", config.getSettingsFilePath());
  }

  public void saveProjectPackagingParams(String project, PackagingParams packagingParams) {
    var projects = this.settings.getProjects();

    var projectSettings = projects.stream()
      .filter(s -> s.getProject().equals(project))
      .findFirst().orElseGet(() -> {
        var s = new ProjectSettings();
        s.setProject(project);
        projects.add(s);
        return s;
      });

    projectSettings.setPackagingParams(packagingParams);
  }
}
