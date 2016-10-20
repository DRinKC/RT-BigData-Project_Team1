package Tutorial6;

import org.openimaj.feature.local.list.LocalFeatureList;
import org.openimaj.image.ImageUtilities;
import org.openimaj.image.MBFImage;
import org.openimaj.image.feature.local.engine.DoGSIFTEngine;
import org.openimaj.image.feature.local.keypoints.Keypoint;

import java.io.*;

/**
 * Created by Naga on 20-09-2016.
 */
public class ObjectFeatureExtraction {
    String path;

    public ObjectFeatureExtraction(String inputImage) {this.path = inputImage;}

    public void extractFeatures() throws IOException {
        String inputLocation = path;
        int count = 0;
        MBFImage mbfImage = ImageUtilities.readMBF(new File(inputLocation));
        DoGSIFTEngine doGSIFTEngine = new DoGSIFTEngine();
        LocalFeatureList<Keypoint> features = doGSIFTEngine.findFeatures(mbfImage.flatten());
        PrintWriter writer = new PrintWriter("output/features.txt", "UTF-8");
        for (int i = 0; i < features.size(); i++) {
            double c[] = features.get(i).getFeatureVector().asDoubleVector();
            writer.write(count + ",");
            for (int j = 0; j < c.length; j++) {
                writer.write(c[j] + " ");
            }
            writer.println("\n");
            count++;
        }
        writer.close();
    }
}

