import Jama.Matrix;
import org.openimaj.feature.local.list.LocalFeatureList;
import org.openimaj.feature.local.matcher.FastBasicKeypointMatcher;
import org.openimaj.feature.local.matcher.consistent.ConsistentLocalFeatureMatcher2d;
import org.openimaj.image.DisplayUtilities;
import org.openimaj.image.FImage;
import org.openimaj.image.ImageUtilities;
import org.openimaj.image.MBFImage;
import org.openimaj.image.colour.RGBColour;
import org.openimaj.image.colour.Transforms;
import org.openimaj.image.feature.local.engine.DoGSIFTEngine;
import org.openimaj.image.feature.local.keypoints.Keypoint;
import org.openimaj.image.renderer.MBFImageRenderer;
import org.openimaj.math.geometry.point.Point2d;
import org.openimaj.math.geometry.transforms.HomographyModel;
import org.openimaj.math.geometry.transforms.HomographyRefinement;
import org.openimaj.math.geometry.transforms.MatrixTransformProvider;
import org.openimaj.math.geometry.transforms.check.TransformMatrixConditionCheck;
import org.openimaj.math.geometry.transforms.estimation.RobustHomographyEstimator;
import org.openimaj.math.model.fit.RANSAC;
import org.openimaj.video.Video;
import org.openimaj.video.xuggle.XuggleVideo;

import java.awt.*;
import java.io.BufferedWriter;
import java.io.File;
import java.io.FileWriter;
import java.io.IOException;
import java.util.List;
//import org.openimaj.math.geometry.shape.Polygon;

/**
 * Created by Naga on 16-09-2016.
 */

public class ObjectMainDetectionYeast {

    public static void main(String args[]) throws IOException {
        ObjectMainDetectionYeastMain objectMainDetectionYeastMain = new ObjectMainDetectionYeastMain();
    }
}

class ObjectMainDetectionYeastMain {
    private ConsistentLocalFeatureMatcher2d<Keypoint> matcher1,matcher2;
    final DoGSIFTEngine engine;
//    private RenderMode renderMode = RenderMode.SQUARE;

    private MBFImage modelImage1,modelImage2;

    public ObjectMainDetectionYeastMain() throws IOException {
        this.engine = new DoGSIFTEngine();
        this.engine.getOptions().setDoubleInitialImage(true);
        this.matcher1 = new ConsistentLocalFeatureMatcher2d<Keypoint>(
                new FastBasicKeypointMatcher<Keypoint>(8));
        this.matcher2 = new ConsistentLocalFeatureMatcher2d<Keypoint>(
                new FastBasicKeypointMatcher<Keypoint>(8));

        //play with these params
        final RobustHomographyEstimator ransac = new RobustHomographyEstimator(0.5, 1500,
                new RANSAC.PercentageInliersStoppingCondition(0.6), HomographyRefinement.NONE,
                new TransformMatrixConditionCheck<HomographyModel>(10000));
        this.matcher1.setFittingModel(ransac);
        this.matcher2.setFittingModel(ransac);
        LoadReferenceObject();
        StartVideo();
    }

    public void StartVideo() throws IOException {
        Video<MBFImage> video = new XuggleVideo(new File("data/yeastvid.mkv"));
        int count1 = 0,count2=0;
        String o1 = "output/features.txt";
        FileWriter fw = new FileWriter(o1);
        BufferedWriter bw = new BufferedWriter(fw);
        for (MBFImage mbfImage : video) {

            //this sets how many sift points we'll be wanting to grab
            //higher the number, less points, lower the number more points
            //the idea is that more sift points might let or image match better but iunno
            this.engine.getOptions().setMagnitudeThreshold(.04F);
            final LocalFeatureList<Keypoint> kpl = this.engine.findFeatures(Transforms.calculateIntensityNTSC(mbfImage));

            final MBFImageRenderer renderer = mbfImage.createRenderer();
            renderer.drawPoints(kpl, RGBColour.MAGENTA, 3);

            if (this.matcher1.findMatches(kpl))
                    /*&& ((MatrixTransformProvider) this.matcher1.getModel()).getTransform().cond() < 1e10 )*/ {
                try {
                    final Matrix boundsToPoly = ((MatrixTransformProvider) this.matcher1.getModel()).getTransform()
                            .inverse();

                    if (modelImage1.getBounds().transform(boundsToPoly).isConvex()) {

                        renderer.drawShape(this.modelImage1.getBounds().transform(boundsToPoly), 3, RGBColour.BLUE);

                        if (count1 <= 10) {
                            List<Point2d> vertices = this.modelImage1.getBounds().transform(boundsToPoly).asPolygon().getVertices();
                            int x[] = new int[4], y[] = new int[4];
                            for (int i = 0; i < vertices.size(); i++) {
                                x[i] = (int) vertices.get(i).getX();
                                y[i] = (int) vertices.get(i).getY();
                            }
                            Polygon polygon = new Polygon(x, y, 4);
                            for (int i = 0; i < kpl.size(); i++) {
                                if (polygon.contains(kpl.get(i).getX(), kpl.get(i).getY())) {
                                    double c[] = kpl.get(i).getFeatureVector().asDoubleVector();
                                    bw.write("0,");
                                    for (int j = 0; j < c.length; j++) {
                                        bw.write(c[j] + " ");
                                    }
                                    bw.newLine();
                                }
                            }

                            count1++;
                        }

                    }
                } catch (Exception e) {
                    System.out.println(e.getMessage());
                }

            }
            if (this.matcher2.findMatches(kpl)
                    && ((MatrixTransformProvider) this.matcher2.getModel()).getTransform().cond() < 1e5 ) {
                try {
                    final Matrix boundsToPoly = ((MatrixTransformProvider) this.matcher2.getModel()).getTransform()
                            .inverse();

                    renderer.drawShape(this.modelImage2.getBounds().transform(boundsToPoly), 3, RGBColour.YELLOW);

                    if(count2 <= 10){
                        if (modelImage2.getBounds().transform(boundsToPoly).isConvex()) {
                            List<Point2d> vertices = this.modelImage2.getBounds().transform(boundsToPoly).asPolygon().getVertices();
                            int x[] = new int[4], y[] = new int[4];
                            for (int i = 0; i < vertices.size(); i++) {
                                x[i] = (int) vertices.get(i).getX();
                                y[i] = (int) vertices.get(i).getY();
                            }
                            Polygon polygon = new Polygon(x, y, 4);
                            for (int i = 0; i < kpl.size(); i++) {
                                if (polygon.contains(kpl.get(i).getX(), kpl.get(i).getY())) {
                                    double c[] = kpl.get(i).getFeatureVector().asDoubleVector();
                                    bw.write("1,");
                                    for (int j = 0; j < c.length; j++) {
                                        bw.write(c[j] + " ");
                                    }
                                    bw.newLine();
                                }
                            }

                            count2++;
                        }

                    }
                } catch (Exception e) {
                    System.out.println(e.getMessage());
                }

            }

            DisplayUtilities.displayName(mbfImage, "Image");
        }
        bw.close();
    }

    public void LoadReferenceObject() {

        final DoGSIFTEngine engine = new DoGSIFTEngine();
        engine.getOptions().setDoubleInitialImage(true);

        try {
            modelImage1 = ImageUtilities.readMBF(new File("data/singleYeast.jpg"));
        } catch (IOException e) {
            e.printStackTrace();
        }

        FImage modelF1 = Transforms.calculateIntensityNTSC(modelImage1);

        //sets the threshold so we get more features from this guy
        this.engine.getOptions().setMagnitudeThreshold(.005F);
        this.matcher1.setModelFeatures(engine.findFeatures(modelF1));

        try {
            modelImage2 = ImageUtilities.readMBF(new File("data/buddingYeast.jpg"));
        } catch (IOException e) {
            e.printStackTrace();
        }

        FImage modelF2 = Transforms.calculateIntensityNTSC(modelImage2);

        //sets it back
        this.engine.getOptions().setMagnitudeThreshold(.04F);
        this.matcher2.setModelFeatures(engine.findFeatures(modelF2));
    }
}

