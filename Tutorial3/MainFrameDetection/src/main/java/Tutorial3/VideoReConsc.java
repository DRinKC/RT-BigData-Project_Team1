package Tutorial3;

import org.openimaj.image.DisplayUtilities;
import org.openimaj.image.ImageUtilities;
import org.openimaj.image.MBFImage;

import java.io.File;
import java.io.IOException;

/**
 * Created by Naga on 07-09-2016.
 * modified by Justin Baraboo on 13-09-2016
 */
public class VideoReConsc {

    public VideoReConsc(){

    }

    public static void createVideo(File path) throws IOException {
        File[] files = path.listFiles();
        //this gets the images to play in order.
        //quick to write and inefficient but the number of key frames should be small
        //so it's pretty okay to do.

        for (int i = 0; i < files.length - 1; i++) {
            int f1 = Integer.parseInt(files[i].getName().split("_")[0]);
            int f2 = Integer.parseInt(files[i + 1].getName().split("_")[0]);
            if (f2 < f1) {
                File temp = files[i];
                files[i] = files[i + 1];
                files[i + 1] = temp;
                i = 0;
            }
        }

        for (int i = 0; i < files.length; i++) {

            System.out.println(files[i].getName());

            MBFImage image = ImageUtilities.readMBF(files[i]);

            //image.drawText("Wild Life", 90, 150, HersheyFont.ASTROLOGY, 20, RGBColour.BLACK);

            DisplayUtilities.displayName(image, "videoFrames");
        }
    }
}