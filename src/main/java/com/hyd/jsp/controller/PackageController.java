package com.hyd.jsp.controller;

import com.hyd.jsp.model.PackagingParams;
import com.hyd.jsp.settings.SettingsRepository;
import com.hyd.jsp.springmvc.Result;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

import java.io.IOException;

@RestController
@RequestMapping("/packaging")
public class PackageController {

  @Autowired
  private SettingsRepository settingsRepository;

  /**
   * 保存项目信息
   */
  @PostMapping("save/{project}")
  public Result saveProject(
    @PathVariable String project, @RequestBody PackagingParams packagingParams
  ) throws IOException {
    this.settingsRepository.saveProjectPackagingParams(project, packagingParams);
    this.settingsRepository.saveSettings();
    return Result.success();
  }
}
