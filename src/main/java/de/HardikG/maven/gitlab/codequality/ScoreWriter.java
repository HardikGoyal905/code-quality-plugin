package de.HardikG.maven.gitlab.codequality;

import java.io.IOException;
import java.io.OutputStream;
import java.io.OutputStreamWriter;
import java.nio.charset.StandardCharsets;

public class ScoreWriter {
    public static void write(double effectiveErrors, OutputStream outputStream) throws IOException {

        try (OutputStreamWriter writer = new OutputStreamWriter(outputStream, StandardCharsets.UTF_8)){
            int diffValue=DiffFileProcess.readDiffValue();

            if(effectiveErrors<0.1){
                writer.write("Congratulations! You have Full Score\n The Number of Lines Changed: "+ diffValue);
            }
            else{
                double score=diffValue/effectiveErrors;
                writer.write("The Score is: " + Double.toString(score)+"\n");
                writer.write("The number of Lines Changed is: " + Integer.toString(diffValue) + "\n");
                writer.write(Double.toString(effectiveErrors)+ " Numbers of Effective Errors");
            }
        }
    }
}