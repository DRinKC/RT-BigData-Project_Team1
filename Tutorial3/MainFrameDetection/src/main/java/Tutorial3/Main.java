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
        mFD.MainFrames(20,30);
        File reconFile = new File("output/realframes");
        VideoReConsc vr = new VideoReConsc();
        vr.createVideo(reconFile);


    }

}
