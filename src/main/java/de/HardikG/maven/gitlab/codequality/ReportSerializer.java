package de.HardikG.maven.gitlab.codequality;

import com.google.gson.Gson;
import com.google.gson.stream.JsonWriter;
import javafx.util.Pair;

import java.io.IOException;
import java.io.OutputStream;
import java.io.OutputStreamWriter;
import java.nio.charset.StandardCharsets;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Locale;

public class ReportSerializer {

  private double effectiveErrors=0.0;

  public double write(List<Finding> findings, OutputStream outputStream) throws IOException {
    HashMap<Finding.Severity, Double> errorType=new HashMap<Finding.Severity, Double>();
    errorType.put(Finding.Severity.INFO, 0.0);
    errorType.put(Finding.Severity.MINOR, 0.1);
    errorType.put(Finding.Severity.MAJOR, 0.5);
    errorType.put(Finding.Severity.CRITICAL, 1.0);

    HashMap<String, ArrayList<Pair<Integer, Integer>>> diffRangeArrayForFilePath=new HashMap<String, ArrayList<Pair<Integer, Integer>>>();


    try (OutputStreamWriter writer = new OutputStreamWriter(outputStream, StandardCharsets.UTF_8)) {

      Gson gson = new Gson().newBuilder().setPrettyPrinting().create();

      JsonWriter jsonWriter = gson.newJsonWriter(writer);

      jsonWriter.beginArray();
      for (Finding finding : findings) {

        if(!diffRangeArrayForFilePath.containsKey(finding.getPath())){
          DiffFileProcess fileProcess = new DiffFileProcess(finding.getPath());
          diffRangeArrayForFilePath.put(finding.getPath(), fileProcess.extractDiffRanges());
        }

        if(!FilterFindings.isValidFinding(diffRangeArrayForFilePath.get(finding.getPath()), finding)){
          continue;
        }

        jsonWriter.beginObject();

        jsonWriter.name("description").value(finding.getDescription());

        jsonWriter.name("fingerprint").value(finding.getFingerprint());

        if (finding.getSeverity() != null) {
          effectiveErrors+=errorType.get(finding.getSeverity());
          jsonWriter.name("severity").value(finding.getSeverity().name().toLowerCase(Locale.ROOT));
        }

        if (finding.getPath() != null) {
          jsonWriter.name("location").beginObject();
          jsonWriter.name("path").value(finding.getPath());
          if (finding.getLine() != null) {
            jsonWriter.name("lines").beginObject();
            jsonWriter.name("begin").value(finding.getLine());
            jsonWriter.name("end").value(finding.getEndLine());
            jsonWriter.endObject();
          }
          jsonWriter.endObject();
        }

        jsonWriter.endObject();

      }
      jsonWriter.endArray();
    }

    return effectiveErrors;
  }
}