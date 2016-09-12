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
import java.util.List;
import java.util.Vector;

/**
 * Created by brendan on 9/11/16.
 */
public class ImageTag {
    public static void main(String args[]) {
        try {
            ClarifaiClient clarifai = new ClarifaiClient("p1vW56NB_zThvTQDwuxqc-pFlPAm8KE6ci9OVQHM",
                    "gaUsW24WpRuhCiW77l3nz3Gwdn2rF-VPTcJlQPai");

            File[] files = {
                    new File("mainframes/26_0.0020387359836901123.jpg"),
                    new File("mainframes/0_0.21355759429153925.jpg"),
                    new File("mainframes/100_0.0015290519877675841.jpg"),
                    new File("mainframes/216_0.0025484199796126403.jpg"),
                    new File("mainframes/354_0.0025484199796126403.jpg")
            };

            List<RecognitionResult> results =
                    clarifai.recognize(new RecognitionRequest(files));

            Vector<Tag> tags = new Vector<Tag>();
            for (int i = 0; i < results.size(); i++) {
                tags.add(results.get(i).getTags().get(0)); // get the first tag and store in tag vector
            }

            for(int i = 0; i < files.length; i++) {
                MBFImage image = ImageUtilities.readMBF(files[i]);
                image.drawText(tags.get(i).getName(), 50, 50, HersheyFont.TIMES_BOLD, 20, RGBColour.CYAN);
                DisplayUtilities.displayName(image, "videoFrames");
                
            }
        }
        catch(Exception e) {
            e.printStackTrace();
        }
    }
}
