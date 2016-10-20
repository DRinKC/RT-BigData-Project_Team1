import org.apache.storm.topology.BasicOutputCollector;
import org.apache.storm.topology.OutputFieldsDeclarer;
import org.apache.storm.topology.base.BaseBasicBolt;
import org.apache.storm.tuple.Fields;
import org.apache.storm.tuple.Tuple;
import org.apache.storm.tuple.Values;
import org.apache.storm.topology.OutputFieldsDeclarer;
import org.openimaj.feature.local.list.LocalFeatureList;
import org.openimaj.image.MBFImage;
import org.openimaj.image.feature.local.engine.DoGSIFTEngine;
import org.openimaj.image.feature.local.keypoints.Keypoint;

import java.util.HashMap;
import java.util.Map;

/**
 * Created by David on 9-Oct-16.
 */
public class FeatureExtraction extends BaseBasicBolt {

    int count = 0;

    public void execute(Tuple tuple, BasicOutputCollector basicOutputCollector) {

        String label = (tuple.getValueByField("Label")).toString();
        if (label == "features")
        {
            count++;
            String[] featureVector = (tuple.getValueByField("Data")).toString().split(" ");
            Integer numOfPoints = featureVector.length;
            String dataToSend = count + ", " + numOfPoints; // outputs (nth vector, num of SIFT points)
            basicOutputCollector.emit(new Values("features", dataToSend));
        }
        else
        {
            basicOutputCollector.emit(new Values(tuple.getValues()));
        }
    }

    public void declareOutputFields(OutputFieldsDeclarer declarer) {
        declarer.declare(new Fields("DataLabel", "DataValue"));
    }
}