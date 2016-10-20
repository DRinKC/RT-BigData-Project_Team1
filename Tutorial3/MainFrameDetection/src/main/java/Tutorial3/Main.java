package Tutorial3;

import java.io.File;
import java.io.IOException;

/**
 * Created by Justin Baraboo on 9/13/2016.
 */
public class Main {

    public static void main(String args[]) throws IOException {

        String path = "data/Ray_MovieS2.mkv";
        MainFrameDetection mFD = new MainFrameDetection(path);
        mFD.Frames();
        //set true if you have all ready found the similarities of frames
        mFD.sethasRun(true);
        mFD.MainFrames(20,30);
        VideoReConsc vr = new VideoReConsc();

        long startTime = System.currentTimeMillis();

        File originalFile = new File("output/frames");
        vr.createOriginal(originalFile);

        long endTime = System.currentTimeMillis();

        long startTime2 = System.currentTimeMillis();

        File reconFile = new File("output/realFrames");
        vr.createVideo(reconFile);

        long endTime2 = System.currentTimeMillis();

        int original = mFD.getNumberImages();
        int after = mFD.getAfterImages();

        System.out.println("The original image had " + original + " frames and took " + (endTime-startTime)
                + " milli seconds and our new video had " + after + " frames and took " + (endTime2-startTime2) + " milli seconds");
    }

}