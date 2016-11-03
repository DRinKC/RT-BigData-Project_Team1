import org.openimaj.feature.local.list.LocalFeatureList;
import org.openimaj.image.ImageUtilities;
import org.openimaj.image.MBFImage;
import org.openimaj.image.feature.local.engine.DoGSIFTEngine;
import org.openimaj.image.feature.local.keypoints.Keypoint;

import java.io.BufferedWriter;
import java.io.File;
import java.io.FileWriter;
import java.io.IOException;

/**
 * Created by Naga on 20-09-2016.
 * modified by Justin Baraboo
 */
public class ObjectFeatureExtractionYeast {
    public static void main(String args[]) throws IOException {
        String inputFolder = "data/";
        String inputImage = "yeast.jpg";
        String outputFolder = "output/";
        String[] IMAGE_CATEGORIES = {"yeast","yeastbud", "red", "whiteCell"};
        int input_class = 3;
        MBFImage mbfImage = ImageUtilities.readMBF(new File(inputFolder + inputImage));
        DoGSIFTEngine doGSIFTEngine = new DoGSIFTEngine();
        LocalFeatureList<Keypoint> features = doGSIFTEngine.findFeatures(mbfImage.flatten());
        FileWriter fw = new FileWriter(outputFolder + IMAGE_CATEGORIES[input_class] + ".txt");
        BufferedWriter bw = new BufferedWriter(fw);

        for (int i = 0; i < features.size(); i++) {
            double c[] = features.get(i).getFeatureVector().asDoubleVector();
            bw.write(input_class + " ");
            for (int j = 0; j < c.length; j++) {
                //needs to be in format
                //<label> <index1>:<value1> <index2>:<value2> ...
                //indices should be one-based and in ascending order
                bw.write((j+1) + ":" + c[j] + " ");
            }
            bw.newLine();
        }

        bw.close();
        System.out.println("I finished and I'm super cool right? Please love me");
    }
}
