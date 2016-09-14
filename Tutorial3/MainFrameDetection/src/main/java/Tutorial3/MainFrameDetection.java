package Tutorial3;

import org.apache.commons.io.FileUtils;
import org.openimaj.feature.local.list.LocalFeatureList;
import org.openimaj.feature.local.matcher.FastBasicKeypointMatcher;
import org.openimaj.feature.local.matcher.LocalFeatureMatcher;
import org.openimaj.feature.local.matcher.consistent.ConsistentLocalFeatureMatcher2d;
import org.openimaj.image.ImageUtilities;
import org.openimaj.image.MBFImage;
import org.openimaj.image.feature.local.engine.DoGSIFTEngine;
import org.openimaj.image.feature.local.keypoints.Keypoint;
import org.openimaj.math.geometry.transforms.estimation.RobustAffineTransformEstimator;
import org.openimaj.math.model.fit.RANSAC;
import org.openimaj.video.Video;
import org.openimaj.video.xuggle.XuggleVideo;

import javax.imageio.ImageIO;
import java.awt.image.BufferedImage;
import java.io.File;
import java.io.IOException;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;



/**
 * Created by Naga on 05-09-2016.
 * modified by Justin on 13-09-2016
 */
public class MainFrameDetection {
    private Video<MBFImage> video;
    //    VideoDisplay<MBFImage> display = VideoDisplay.createVideoDisplay(video);
    private List<MBFImage> imageList = new ArrayList<MBFImage>();
    private List<Long> timeStamp = new ArrayList<Long>();
    private List<Double> mainPoints = new ArrayList<Double>();
    private String path;
    private int numberImages = 0;
    private int afterImages = 0;
    private boolean hasRun = false;


    public MainFrameDetection(String path){
        this.path = path;
    }

    /*
    public static void main(String args[]){
        String path = "data/Ray_MovieS2.mkv";
        System.out.println("gonna call Frames");
        Frames(path);
        MainFrames();
    }
    */

    //this gets frames
    public void Frames(){
        System.out.println("gonna call Xuggle");

        video = new XuggleVideo(new File(path));
        System.out.println("made it");

//        VideoDisplay<MBFImage> display = VideoDisplay.createVideoDisplay(video);
        int j=0;
        System.out.println("Initializing frame getting");
        for (MBFImage mbfImage : video) {
            BufferedImage bufferedFrame = ImageUtilities.createBufferedImageForDisplay(mbfImage);
            j++;
            System.out.println("On image " + j);
            String name = "output/frames/new" + j + ".jpg";
            File outputFile = new File(name);
            try {

                ImageIO.write(bufferedFrame, "jpg", outputFile);

            } catch (IOException e) {
                e.printStackTrace();
            }
            MBFImage b = mbfImage.clone();
            imageList.add(b);
            timeStamp.add(video.getTimeStamp());
        }

        System.out.println("The total frames are:" + j);
        numberImages = j;
    }


    //this decides main frames
    public void MainFrames(){
        System.out.println("ImageList.size = " + imageList.size());

        //parallelize this plz
        for (int i=0; i<imageList.size() - 1; i++)
        {
            System.out.println("ImageList we on = " + i);

            MBFImage image1 = imageList.get(i);
            MBFImage image2 = imageList.get(i+1);
            DoGSIFTEngine engine = new DoGSIFTEngine();
            LocalFeatureList<Keypoint> queryKeypoints = engine.findFeatures(image1.flatten());
            LocalFeatureList<Keypoint> targetKeypoints = engine.findFeatures(image2.flatten());
            RobustAffineTransformEstimator modelFitter = new RobustAffineTransformEstimator(5.0, 1500,
                    new RANSAC.PercentageInliersStoppingCondition(0.5));
            LocalFeatureMatcher<Keypoint> matcher = new ConsistentLocalFeatureMatcher2d<Keypoint>(
                    new FastBasicKeypointMatcher<Keypoint>(8), modelFitter);
            matcher.setModelFeatures(queryKeypoints);
            matcher.findMatches(targetKeypoints);
            double size = matcher.getMatches().size();
            mainPoints.add(size);
            System.out.println("Main points added " + size);
        }
        Double max = Collections.max(mainPoints);

        for(int i=0; i<mainPoints.size(); i++){
            //let's see what happens if we increase the .01 value
            //I think we get more points
            if(((mainPoints.get(i))/max < 0.50) || i==0){
                Double name1 = mainPoints.get(i)/max;
                BufferedImage bufferedFrame = ImageUtilities.createBufferedImageForDisplay(imageList.get(i+1));
                String name = "output/mainframes/" + i + "_" + name1.toString() + ".jpg";
                File outputFile = new File(name);
                try {
                    ImageIO.write(bufferedFrame, "jpg", outputFile);
                } catch (IOException e) {
                    e.printStackTrace();
                }


                numberImages++;
            }
        }
    }

    public void MainFrames(double sensitivity){
        System.out.println("ImageList.size = " + imageList.size());

        /*SparkConf sparkConf = new SparkConf().setMaster("local").setAppName("MainFrame");
        JavaSparkContext sc = new JavaSparkContext(sparkConf);
        sc.setLogLevel("FATAL");
        JavaRDD<MBFImage> imagesRDD = sc.parallelize(imageList);
        List<Tuple2<MBFImage,MBFImage>> imageCompare = new ArrayList<Tuple2<MBFImage,MBFImage>>();
        for (int i=0; i<imageList.size() - 1; i++){
            MBFImage image1 = imageList.get(i);
            MBFImage image2 = imageList.get(i+1);
            imageCompare.add(new Tuple2<MBFImage,MBFImage>(image1,image2));
        }*/


        for (int i=0; i<imageList.size() - 1; i++)
        {
            System.out.println("ImageList we on = " + i);
            MBFImage image1 = imageList.get(i);
            MBFImage image2 = imageList.get(i+1);
            DoGSIFTEngine engine = new DoGSIFTEngine();
            LocalFeatureList<Keypoint> queryKeypoints = engine.findFeatures(image1.flatten());
            LocalFeatureList<Keypoint> targetKeypoints = engine.findFeatures(image2.flatten());
            RobustAffineTransformEstimator modelFitter = new RobustAffineTransformEstimator(5.0, 1500,
                    new RANSAC.PercentageInliersStoppingCondition(0.5));
            LocalFeatureMatcher<Keypoint> matcher = new ConsistentLocalFeatureMatcher2d<Keypoint>(
                    new FastBasicKeypointMatcher<Keypoint>(8), modelFitter);
            matcher.setModelFeatures(queryKeypoints);
            matcher.findMatches(targetKeypoints);
            double size = matcher.getMatches().size();
            mainPoints.add(size);
            //System.out.println("Main points added " + size);
        }
        Double max = Collections.max(mainPoints);
        for(int i=0; i<mainPoints.size(); i++){
            //let's see what happens if we increase the .01 value
            //I think we get more points
            if(((mainPoints.get(i))/max < sensitivity) || i==0){
                Double name1 = mainPoints.get(i)/max;
                BufferedImage bufferedFrame = ImageUtilities.createBufferedImageForDisplay(imageList.get(i+1));
                String name = "output/mainframes/" + i + "_" + name1.toString() + ".jpg";
                File outputFile = new File(name);
                try {
                    ImageIO.write(bufferedFrame, "jpg", outputFile);
                } catch (IOException e) {
                    e.printStackTrace();
                }

                numberImages++;

            }
        }
    }


    //need to add a delete directory realimages if it all ready exists needs to be implemented.
    public void MainFrames(int lowBound){

        if(!hasRun){
            //this will find all the similarities, we can pick which ones we don't want later
            //it'll take the same amount of time.

            MainFrames(1.0);
        }
        else{

            File path = new File("output/mainframes");
            File[] files = path.listFiles();

            if(lowBound > files.length){
                lowBound = files.length;
                //throw error about lowBound being too big
            }

            double candSens = Double.parseDouble(files[lowBound-1].getName().split("_")[1].split(".jpg")[0]);

            //sort images by sensitivity thing
            //old way, would need to do inner sorting else frames would be in out of order when chosen
/*
            for (int i = 0; i < files.length - 1; i++) {
                //for(String s : files[i].getName().split("_")[1].split("jpg")){
                 //   System.out.println(s);
                //}
                //System.out.println(files[i].getName().split("_")[1].split(".") + "   cats");
                double f1 = Double.parseDouble(files[i].getName().split("_")[1].split(".jpg")[0]);
                double f2 = Double.parseDouble(files[i + 1].getName().split("_")[1].split(".jpg")[0]);
                if (f2 < f1) {
                    File temp = files[i];
                    files[i] = files[i + 1];
                    files[i + 1] = temp;
                    i = 0;
                }
            }
            //need to add a delete directory if images function to this.
            //grab images until greater than lowerbound that have all same sensitivity
            for(int i = 0; i < lowBound; i++){
                String name = "output/realFrames/" + files[i].getName();
                File outputFile = new File(name);
                try {
                    MBFImage image = ImageUtilities.readMBF(files[i]);
                    BufferedImage bufferedFrame = ImageUtilities.createBufferedImageForDisplay(image);
                    ImageIO.write(bufferedFrame, "jpg", outputFile);
                } catch (IOException e) {
                    e.printStackTrace();
                }
            }
            */
            for(int i = 0; i < files.length; i++){
                double test = Double.parseDouble(files[i].getName().split("_")[1].split(".jpg")[0]);
                if(test <= candSens){
                    String name = "output/realFrames/" + files[i].getName();
                    File outputFile = new File(name);
                    try {
                        MBFImage image = ImageUtilities.readMBF(files[i]);
                        BufferedImage bufferedFrame = ImageUtilities.createBufferedImageForDisplay(image);
                        ImageIO.write(bufferedFrame, "jpg", outputFile);
                    } catch (IOException e) {
                        e.printStackTrace();
                    }
                    afterImages++;
                }
            }


        }
        /*
        double sensitivity = .01;
        MainFrames(sensitivity);
        System.out.println(numberImages + "is main points size and stuff I think we need to recurse");
        while(numberImages < lowBound) {
            sensitivity += .05;
            mainPoints = new ArrayList<Double>();
            numberImages=0;
            MainFrames(sensitivity);
            System.out.println("Increasing Sensitivity because MainFrames size is: " + numberImages);
        }*/
    }

    /*
    sets an upper bound and a lower bound for number of frames to get.
     */
    public void MainFrames(int lowBound, int upperBound){

        if(!hasRun){
            //this will find all the similarities, we can pick which ones we don't want later
            //it'll take the same amount of time.

            MainFrames(1.0);
        }
        else{

            File path = new File("output/mainframes");
            File[] files = path.listFiles();

            if(lowBound > files.length){
                lowBound = files.length;
                //throw error about lowBound being too big
            }

            double candSens = Double.parseDouble(files[lowBound-1].getName().split("_")[1].split(".jpg")[0]);

            File realFrames = new File("output/realFrames");
            try {
                FileUtils.cleanDirectory(realFrames);
            } catch (IOException e) {
                e.printStackTrace();
            }


            for(int i = 0; i < files.length; i++){
                double test = Double.parseDouble(files[i].getName().split("_")[1].split(".jpg")[0]);
                if(test <= candSens){
                    String name = "output/realFrames/" + files[i].getName();
                    File outputFile = new File(name);
                    try {
                        MBFImage image = ImageUtilities.readMBF(files[i]);
                        BufferedImage bufferedFrame = ImageUtilities.createBufferedImageForDisplay(image);
                        ImageIO.write(bufferedFrame, "jpg", outputFile);
                    } catch (IOException e) {
                        e.printStackTrace();
                    }
                    afterImages++;
                    System.out.println(afterImages);
                    if(afterImages == upperBound){
                        System.out.println("ending it");
                        i = files.length;
                    }
                }
            }
        }
    }

    public void sethasRun(Boolean t){
        hasRun = t;
    }

    public int getNumberImages(){
        return numberImages;
    }

    public int getAfterImages(){
        return afterImages;
    }


}