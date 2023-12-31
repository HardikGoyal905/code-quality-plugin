package de.HardikG.maven.gitlab.codequality.spotbugs;

import de.HardikG.maven.gitlab.codequality.Finding;
import de.HardikG.maven.gitlab.codequality.FindingProvider;
import de.HardikG.maven.gitlab.codequality.Logger;
import de.HardikG.maven.gitlab.codequality.spotbugs.BugCollection.BugInstance;
import jakarta.xml.bind.JAXBContext;
import jakarta.xml.bind.JAXBException;
import jakarta.xml.bind.Unmarshaller;
import java.io.File;
import java.io.IOException;
import java.io.InputStream;
import java.nio.file.Path;
import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;
import org.apache.maven.project.MavenProject;

public class SpotbugsFindingProvider implements FindingProvider {

  private final MavenProject project;
  private final File repositoryRoot;
  private final Logger log;

  public SpotbugsFindingProvider(MavenProject project, File repositoryRoot, Logger log) {
    this.project = project;
    this.repositoryRoot = repositoryRoot;
    this.log = log;
  }

  @Override
  public String getName() {
    return "SpotBugs";
  }

  @Override
  public List<Finding> getFindings(InputStream stream) {

    try {

      BugCollection bugCollection;

      if(stream.available() > 0) {
        JAXBContext jaxbContext = JAXBContext.newInstance(BugCollection.class);
        Unmarshaller unmarshaller = jaxbContext.createUnmarshaller();
        bugCollection = (BugCollection) unmarshaller.unmarshal(stream);
      }
      else{
        bugCollection = new BugCollection();
      }

      return bugCollection.getBugInstance().stream()
          .map(this::transformBugInstance)
          .collect(Collectors.toList());

    } catch (JAXBException e) {
      throw new IllegalStateException(e);
    } catch (IOException e) {
      throw new RuntimeException(e);
    }

  }


  private Finding transformBugInstance(BugInstance bugInstance) {

    Optional<SourcePosition> sourcePosition = getSourcePosition(bugInstance);

    Finding finding = new Finding();
    finding.setDescription(String.format("%s: %s", getName(), bugInstance.getShortMessage()));
    finding.setFingerprint(bugInstance.getInstanceHash());
    finding.setSeverity(getSeverity(bugInstance.getPriority()));
    finding.setPath(sourcePosition.map(this::getRepositoryRelativePath).orElse("ERROR"));
    finding.setLine(sourcePosition.map(SourcePosition::getLine).orElse(1));
    finding.setEndLine(sourcePosition.map(SourcePosition::getEndLine).orElse(1));

    return finding;

  }

  private String getRepositoryRelativePath(SourcePosition sourcePosition) {

    Path absolutePath = getAbsolutePath(sourcePosition.getSourcePath()).orElse(null);
    if (absolutePath == null) {
      log.warn("Unable to find file for path: {}", sourcePosition.getSourcePath());
      return sourcePosition.getSourcePath();
    }

//    System.out.println("Spotbugs"+absolutePath+repositoryRoot);
//    System.out.println(repositoryRoot.toPath().relativize(absolutePath).toString());

    return repositoryRoot.toPath().relativize(absolutePath).toString();

  }

  private Optional<Path> getAbsolutePath(String path) {

    for (String compileSourceRoot : project.getCompileSourceRoots()) {
      Path absolutePath = Path.of(compileSourceRoot, path).toAbsolutePath();
      if (absolutePath.toFile().exists()) {
        return Optional.of(absolutePath);
      }
    }
    return Optional.empty();
  }

  private Optional<SourcePosition> getSourcePosition(BugInstance bugInstance) {

    return bugInstance.getClazzOrTypeOrMethod().stream()
        .filter(o -> o instanceof SourceLine)
        .map(o -> (SourceLine) o)
        .findFirst()
        .map(sourceLine -> new SourcePosition(
            sourceLine.getSourcepath(),
            sourceLine.getStart() != null ? sourceLine.getStart() : 1,
            sourceLine.getEnd() != null ? sourceLine.getEnd() : 1
        ));

  }

  private static Finding.Severity getSeverity(short priority) {
    switch (priority) {
      case 1:  // HIGH
        return Finding.Severity.CRITICAL;
      case 2:  // NORMAL
        return Finding.Severity.MAJOR;
      case 3:  // LOW
        return Finding.Severity.MINOR;
      case 4:  // EXP
      case 5:  // IGNORE
      default:
        return Finding.Severity.INFO;
    }
  }

  private static class SourcePosition {

    private final String sourcePath;
    private final int line;

    private final int endLine;

    public SourcePosition(String sourcePath, int line, int endLine) {
      this.sourcePath = sourcePath;
      this.line = line;
      this.endLine = endLine;
    }

    public String getSourcePath() {
      return sourcePath;
    }

    public int getLine() {
      return line;
    }

    public int getEndLine() { return endLine; }

  }

}
