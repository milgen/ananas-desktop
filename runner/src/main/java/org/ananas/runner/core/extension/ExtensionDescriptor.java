package org.ananas.runner.core.extension;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;

@JsonIgnoreProperties(ignoreUnknown = true)
public class ExtensionDescriptor {
  public String name;
  public String version;
  public String minAnanasVersion;
  public String author;
  public String url;
  public String sha1;
}
