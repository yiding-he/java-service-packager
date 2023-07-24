package com.hyd.jsp.model;

import lombok.Data;

import java.util.ArrayList;
import java.util.List;

@Data
public class Settings {

  private List<ProjectSettings> projects = new ArrayList<>();
}
