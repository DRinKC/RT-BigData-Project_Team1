package Tutorial3;

import java.io.File;
import java.io.IOException;

/**
 * Created by Justin Baraboo on 9/13/2016.
 */
public class Main {
    public static int originalTime;
    public static int originalFrames;
    public static double fps;
    public static void main(String args[]) throws IOException {

        String path = "data/Ray_MovieS2.mkv";
        MainFrameDetection mFD = new MainFrameDetection(path);
        mFD.Frames();
        originalTime = 14; // input this value
        originalFrames = mFD.MainFrames(20,30);
        fps = originalFrames / originalTime;
        File reconFile = new File("output/realframes");
        VideoReConsc vr = new VideoReConsc();
        vr.createVideo(reconFile, fps);
        System.out.println("The original video had "+originalFrames+" frames and ran for about "+originalTime+" seconds.");

    }

}
