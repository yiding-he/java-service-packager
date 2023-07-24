package com.hyd.jsp.model;

import lombok.Data;

@Data
public class ProjectSettings {

  private String project;

  private PackagingParams packagingParams = new PackagingParams();
}
