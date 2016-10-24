import org.apache.storm.topology.BasicOutputCollector;
import org.apache.storm.topology.OutputFieldsDeclarer;
import org.apache.storm.topology.base.BaseBasicBolt;
import org.apache.storm.tuple.Fields;
import org.apache.storm.tuple.Tuple;
import org.apache.storm.tuple.Values;
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
import java.util.Map;

/**
 * Created by DR042460 on 10/9/2016.
 */
public class MainFrameExtraction extends BaseBasicBolt {

    public void execute(Tuple tuple, BasicOutputCollector basicOutputCollector) {

        String dataToSend = null;
        String rawData = (tuple.getValueByField("DataValue")).toString();
        if (rawData.contains(":")) {
            String[] dividedData = rawData.split(":");
            if (dividedData[0].contains("OF"))
                dataToSend = "OriginalFrames: "+dividedData[1];
            else if (dividedData[0].contains("OT"))
                dataToSend = "OriginalLength: "+dividedData[1];
            else if (dividedData[0].contains("MF"))
                dataToSend = "MainFrames: "+dividedData[1];
            else if (dividedData[0].contains("MT"))
                dataToSend = "NewLength: "+dividedData[1];
            if (dataToSend != null)
                basicOutputCollector.emit(new Values(dataToSend));
        }
        else
        {
            basicOutputCollector.emit(new Values(tuple.getValueByField("Data").toString()));
        }
    }

    public void declareOutputFields(OutputFieldsDeclarer declarer) {
        declarer.declare(new Fields("message"));
    }
}