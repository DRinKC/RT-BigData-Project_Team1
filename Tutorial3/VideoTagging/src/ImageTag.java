import com.clarifai.api.ClarifaiClient;
import com.clarifai.api.RecognitionRequest;
import com.clarifai.api.RecognitionResult;
import com.clarifai.api.Tag;
import org.openimaj.image.DisplayUtilities;
import org.openimaj.image.FImage;
import org.openimaj.image.ImageUtilities;
import org.openimaj.image.MBFImage;
import org.openimaj.image.colour.RGBColour;
import org.openimaj.image.typography.hershey.HersheyFont;
import org.openimaj.video.Video;
import org.openimaj.video.VideoDisplay;
import org.openimaj.video.xuggle.XuggleVideo;

import java.io.File;
import java.util.*;
import java.util.concurrent.TimeUnit;

/**
 * Created by brendan on 9/11/16.
 */
public class ImageTag {
    public static void main(String args[]) {
        try {
            ClarifaiClient clarifai = new ClarifaiClient("p1vW56NB_zThvTQDwuxqc-pFlPAm8KE6ci9OVQHM",
                    "gaUsW24WpRuhCiW77l3nz3Gwdn2rF-VPTcJlQPai");

            File[] keyFrames = new File("realFrames").listFiles();
            File path = new File("frames");
            File[] allFrames = path.listFiles();
            Arrays.sort(allFrames);

            List<RecognitionResult> results =
                    clarifai.recognize(new RecognitionRequest(keyFrames));

            Vector<Tag> tags = new Vector<Tag>();
            for (int i = 0; i < results.size(); i++) {
                tags.add(results.get(i).getTags().get(2)); // get the second tag and store in tag vector
            }

            Vector<Integer> transIndex = new Vector<Integer>();
            for(int i = 0; i < keyFrames.length; i++) {
                String fname = keyFrames[i].toString();
                String index = fname.split("_")[0].split("/")[1];
                transIndex.add(Integer.parseInt(index));
            }
            Collections.sort(transIndex);

            for(int i = 0; i < allFrames.length; i++) {
                if (!transIndex.isEmpty() && i == transIndex.get(0)) {
                    transIndex.remove(0);
                    tags.remove(0);
                }
                MBFImage image = ImageUtilities.readMBF(allFrames[i]);
                image.drawText(tags.get(0).getName(), 50, 50, HersheyFont.TIMES_BOLD, 20, RGBColour.CYAN);
                DisplayUtilities.displayName(image, "videoFrames");
                Thread.sleep(100);

            }
        }
        catch(Exception e) {
            e.printStackTrace();
        }
    }
}
