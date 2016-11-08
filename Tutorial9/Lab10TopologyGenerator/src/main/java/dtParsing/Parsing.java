package dtParsing;

import com.google.common.base.Joiner;

import java.io.*;
import java.util.ArrayList;
import java.util.List;

/**
 * Created by Naga on 18-10-2016.
 */
public class Parsing {

    public static void main(String args[]) throws IOException {
        String label = "1.0";
        String inputPath = "data/data.txt"; //Input path of decision tree model from Spark-ML-Lib
        String outputPath = "data/Class1.txt"; //Output path for each class label
        GeneratePathForClass(inputPath, outputPath, label);
    }

    public Parsing(String model, String outputPath, String label){
        try {
            GeneratePathForClass(model, outputPath, label);
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    public static String addBrackets(String line) {
        int section = line.indexOf("feature") + 7;
        if (line.contains("<")) {
            int op = line.indexOf("<");
            String numsec = line.substring(section, op);
            String newnum = "[" + numsec.trim() + "] ";
            line = line.replaceFirst(numsec, newnum);
        } else if (line.contains(">")) {
            int op = line.indexOf(">");
            String numsec = line.substring(section, op);
            String newnum = "[" + numsec.trim() + "] ";
            line = line.replaceFirst(numsec, newnum);
        }
        return line;
    }


    public static void GeneratePathForClass(String model, String outputPath, String label) throws IOException {
        Boolean check = false;
        List<String> tree = new ArrayList<String>();
        String[] modelArray = model.toString().split("\n");
        for(int i=0; i<modelArray.length;i++) {
            if (modelArray[i].contains("If")) {
                modelArray[i] = addBrackets(modelArray[i]);
                String labelChange = modelArray[i].replaceAll("If", "if");
                System.out.println(labelChange);
                tree.add(labelChange);
                continue;
            }

            if (modelArray[i].contains("Else")) {
                modelArray[i] = addBrackets(modelArray[i]);
                String labelChange = modelArray[i].replaceAll("Else","else if");
                System.out.println(labelChange);
                tree.add(labelChange);
                continue;
            }

            if (modelArray[i].contains(label)) {
                String labelChange = modelArray[i].replaceAll("Predict: "+label, "return true;");
                System.out.println(labelChange);
                tree.add(labelChange);
                continue;
            }
            else if (modelArray[i].contains("Predict")) {
                int ip = modelArray[i].indexOf("Predict");
                String labelChange = modelArray[i].replaceAll(modelArray[i].substring(ip,
                        modelArray[i].length()),"return false;");
                System.out.println(labelChange);
                tree.add(labelChange);
                continue;
            }

//            /*
//            Check for the leaf nodes
//             */
//            if (modelArray[i].contains("return")) {
//                /*
//                Modify the ELSE-IF to IF if we find the desired class label as return for the path
//                 */
//                if (modelArray[i].contains("Predict: " + label)) {
//                    if (check == true) {
//                        String l = tree.get(tree.size()-1);
//                        tree.remove(tree.size()-1);
//                        String newl = l.replaceAll("else if", "if");
//                        tree.add(newl);
//                        check = false;
//                    }
//                    String labelChange = modelArray[i].replaceAll(label, "true");
//                    tree.add(labelChange);
//                }
//
//                /*
//                If leaf node belongs to other class, then remove it and corresponding path
//                 */
//                else {
//                    tree.remove(tree.size() - 1);
//                    check = true;
//                }
//            }
            /*
            Making output compatiable to use directly in Java methods
             */
        }


        Joiner joiner = Joiner.on("\n").useForNull("null");
        String output = joiner.join(tree);


        /*
        Saving output to a File
         */
        FileWriter fw = new FileWriter(new File(outputPath));
        BufferedWriter bw = new BufferedWriter(fw);
        bw.write(output);
        bw.close();
    }

}