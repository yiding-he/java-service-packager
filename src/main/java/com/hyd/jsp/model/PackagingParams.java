package com.hyd.jsp.model;

import lombok.Data;

@Data
public class PackagingParams {

  private SourceParams sourceParams = new SourceParams();

  private BuildParams buildParams = new BuildParams();
}
