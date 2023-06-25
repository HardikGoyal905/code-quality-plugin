package de.HardikG.maven.gitlab.codequality;

import javafx.util.Pair;

import java.util.ArrayList;

public class FilterFindings {
    public static boolean isValidFinding(ArrayList<Pair<Integer, Integer>> diffRanges, Finding finding) {
        int findingStartAt = finding.getLine();
        int findingEndAt = finding.getEndLine();

        for (Pair<Integer, Integer> diffRange : diffRanges) {
            int diffRangeStartAt = diffRange.getKey();
            int diffRangeEndAt = diffRange.getValue();

            if (!(findingEndAt < diffRangeStartAt || findingStartAt > diffRangeEndAt)) {
                return true;
            }
        }

        return false;
    }

}
