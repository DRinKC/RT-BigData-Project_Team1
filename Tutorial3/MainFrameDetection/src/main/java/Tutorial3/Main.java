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
        File reconFile = new File("output/realframes");
        VideoReConsc vr = new VideoReConsc();
        vr.createVideo(reconFile);

        int originalTime = 14;
        int original = mFD.getNumberImages();
        double fps = original / originalTime;
        int after = mFD.getAfterImages();
        int afterTime = (int) (after / fps);

        System.out.println("The original video had "+original+" frames and ran for about "+originalTime+" seconds.");
        System.out.println("The new video has "+after+" frames and runs for about "+afterTime+" seconds");
    }

}
