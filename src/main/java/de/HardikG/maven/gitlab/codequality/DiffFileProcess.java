package de.HardikG.maven.gitlab.codequality;

import javafx.util.Pair;

import java.io.FileInputStream;
import java.io.IOException;
import java.util.ArrayList;
import java.util.Scanner;
import java.util.regex.Pattern;

public class DiffFileProcess {
    private String path;

    private final String regex="^@@.*@@.*";

    private Pattern pattern;

    private static final String diffValuePath="target/diffValue.txt";

    public DiffFileProcess(String path){
        this.path="target/diffForFiles/"+path;
        pattern=Pattern.compile(regex);
    }



    public ArrayList<Pair<Integer, Integer>> extractDiffRanges(){
        ArrayList<Pair<Integer, Integer>> diffRanges=new ArrayList<Pair<Integer, Integer>>();
        try{
            FileInputStream fis=new FileInputStream(path);
            Scanner sc=new Scanner(fis);

            while(sc.hasNextLine()){
                String toBeMatched=sc.nextLine();
                int n=toBeMatched.length();

                if(pattern.matcher(toBeMatched).find()){
                    String[] numbers= new String[4];

                    boolean newOneInNeed=true;
                    int lastIndexEncountered=-1;
                    for(int i=3; i<n; i++){
                        if(toBeMatched.charAt(i)>='0' && toBeMatched.charAt(i)<='9'){
                            if(newOneInNeed){
                                newOneInNeed=false;
                                lastIndexEncountered++;

                                numbers[lastIndexEncountered]=new String();
                                numbers[lastIndexEncountered]+=toBeMatched.charAt(i);
                            }
                            else{
                                numbers[lastIndexEncountered]+=toBeMatched.charAt(i);
                            }
                        }
                        else{
                            newOneInNeed=true;

                            if(lastIndexEncountered==3){
                                break;
                            }
                        }
                    }

                    int start=Integer.parseInt(numbers[2]);
                    int end=start+Integer.parseInt(numbers[3]);

                    Pair<Integer, Integer> range= new Pair<Integer, Integer>(start, end);
                    diffRanges.add(range);
                }
            }
            sc.close();
        }
        catch(IOException e) {
            e.printStackTrace();
        }

        return diffRanges;
    }

    public static int readDiffValue(){
        try {
            FileInputStream fis = new FileInputStream(diffValuePath);
            Scanner sc=new Scanner(fis);

            while(sc.hasNextLine()){
                String diffValueString=sc.nextLine();
                int diffValue=Integer.parseInt(diffValueString);
                return diffValue;
            }
        }
        catch(IOException e) {
            e.printStackTrace();
        }

        return 0; // if diffValue.txt not found
    }

}
