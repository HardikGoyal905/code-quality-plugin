package de.HardikG.maven.gitlab.codequality;

public class Finding {

  public enum Severity {
    INFO,
    MINOR,
    MAJOR,
    CRITICAL,
    BLOCKER
  }

  private String description;
  private String fingerprint;
  private Severity severity;

  private String path;
  private Integer line;

  private Integer endLine;

  public String getDescription() {
    return description;
  }

  public void setDescription(String description) {
    this.description = description;
  }

  public String getFingerprint() {
    return fingerprint;
  }

  public void setFingerprint(String fingerprint) {
    this.fingerprint = fingerprint;
  }

  public Severity getSeverity() {
    return severity;
  }

  public void setSeverity(Severity severity) {
    this.severity = severity;
  }

  public String getPath() {
    return path;
  }

  public void setPath(String path) {
    this.path = path;
  }

  public Integer getLine() {
    return line;
  }

  public void setLine(Integer line) {
    this.line = line;
  }

  public Integer getEndLine() {
    return endLine;
  }

  public void setEndLine(Integer endLine) {
    this.endLine = endLine;
  }

}
