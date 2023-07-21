package com.hyd.jsp.controller;

import com.hyd.jsp.model.PackagingParams;
import com.hyd.jsp.springmvc.Result;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/packaging")
public class PackageController {

  /**
   * 保存项目信息
   */
  @PostMapping("save/{project}")
  public Result saveProject(@PathVariable String project, @RequestBody PackagingParams params) {
    return Result.success();
  }
}
