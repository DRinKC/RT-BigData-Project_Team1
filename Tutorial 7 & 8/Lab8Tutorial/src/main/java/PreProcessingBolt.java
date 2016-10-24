import org.apache.storm.task.TopologyContext;
import org.apache.storm.topology.BasicOutputCollector;
import org.apache.storm.topology.OutputFieldsDeclarer;
import org.apache.storm.topology.base.BaseBasicBolt;
import org.apache.storm.tuple.Fields;
import org.apache.storm.tuple.Tuple;
import org.apache.storm.tuple.Values;
import com.migcomponents.migbase64.Base64;

import org.openimaj.image.ImageUtilities;
import org.openimaj.image.MBFImage;
import java.awt.image.BufferedImage;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Map;
import java.io.ByteArrayInputStream;
import javax.imageio.ImageIO;
import java.util.List;

/**
 * Created by ry6d3 on 9/28/2016.
 */
public class PreProcessingBolt extends BaseBasicBolt {

    int count1;

    @Override
    public void prepare(Map stormConf, TopologyContext context) {
        super.prepare(stormConf, context);
        count1 = 0;
    }

    @Override
    public void execute(Tuple input, BasicOutputCollector collector) {

        try {
            String inputData = input.getString(0);
            String[] dataToSend = inputData.split(";");
            for (int i = 0; i < dataToSend.length; i++) {
                if (dataToSend[i].contains(":")) // contains metadata
                    collector.emit(new Values("metadata", dataToSend[i]));
                else // contains feature data
                    collector.emit(new Values("features", dataToSend[i]));
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    @Override
    public void declareOutputFields(OutputFieldsDeclarer declarer) {
        declarer.declare(new Fields("Label", "Data"));
    }


}
