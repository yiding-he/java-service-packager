package com.hyd.jsp.controller;

import com.hyd.jsp.model.PackagingParams;
import com.hyd.jsp.settings.SettingsRepository;
import com.hyd.jsp.springmvc.Result;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
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
  @Operation(
    tags = {"构建配置接口" },
    operationId = "saveProjectPackagingParams",
    summary = "保存构建配置",
    description = "保存指定项目的构建配置"
  )
  public Result saveProject(
    @RequestBody
    @Parameter(description = "要保存的配置内容")
    PackagingParams packagingParams,
    @PathVariable
    @Parameter(description = "项目名称", example = "demo-project")
    String project,
    @RequestParam(value = "others", required = false)
    @Parameter(hidden = true)
    String others
  ) throws IOException {
    this.settingsRepository.saveProjectPackagingParams(project, packagingParams);
    this.settingsRepository.saveSettings();
    return Result.success();
  }
}
