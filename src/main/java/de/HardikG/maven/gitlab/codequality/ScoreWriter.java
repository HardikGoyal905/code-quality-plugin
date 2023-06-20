package de.HardikG.maven.gitlab.codequality;

import java.io.IOException;
import java.io.OutputStream;
import java.io.OutputStreamWriter;
import java.nio.charset.StandardCharsets;

public class ScoreWriter {
    public static void write(double effectiveErrors, OutputStream outputStream) throws IOException {

        try (OutputStreamWriter writer = new OutputStreamWriter(outputStream, StandardCharsets.UTF_8)){
            writer.write(Double.toString(effectiveErrors));
        }
    }
}