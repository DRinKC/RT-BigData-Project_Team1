import org.apache.log4j.BasicConfigurator;
import org.apache.storm.Config;
import org.apache.storm.LocalCluster;
import org.apache.storm.StormSubmitter;
import org.apache.storm.generated.StormTopology;
import org.apache.storm.kafka.*;
import org.apache.storm.kafka.bolt.*;
import org.apache.storm.kafka.KeyValueSchemeAsMultiScheme;
import org.apache.storm.kafka.bolt.KafkaBolt;
import org.apache.storm.kafka.bolt.mapper.FieldNameBasedTupleToKafkaMapper;
import org.apache.storm.kafka.bolt.selector.DefaultTopicSelector;
import org.apache.storm.topology.TopologyBuilder;
import org.apache.storm.tuple.Fields;

import java.util.Properties;

/**
 * Created by David on 10-10-16
 */
public class StormKafkaMain {
    private static final String KAFKA_TOPIC ="Group01Topic";
    private static final String KAFKA_TOPIC_B = "Group01";
    public static void main(String[] args) {
        // TODO Auto-generated method stub
        BasicConfigurator.configure();

        if (args != null && args.length > 0)
        {
            try {
                StormSubmitter.submitTopology(
                        args[0],
                        createConfig(false),
                        createTopology());
            } catch (Exception e) {
                // TODO Auto-generated catch block
                e.printStackTrace();
            }
        }
        else
        {
            LocalCluster cluster = new LocalCluster();
            cluster.submitTopology(
                    "FeatureExtractionTopology",
                    createConfig(true),
                    createTopology());
            try {
                Thread.sleep(60000);
            } catch (InterruptedException e) {
                // TODO Auto-generated catch block
                e.printStackTrace();
            }
            cluster.shutdown();
        }
    }

    private static StormTopology createTopology()
    {
        SpoutConfig kafkaConf = new SpoutConfig(
                new ZkHosts("localhost:2181"),
                KAFKA_TOPIC,
                "/kafka",
                "KafkaSpout");
        Properties props = new Properties();
        props.put("bootstrap.servers", "localhost:9092");
        props.put("acks", "1");
        props.put("key.serializer", "org.apache.kafka.common.serialization.StringSerializer");
        props.put("value.serializer", "org.apache.kafka.common.serialization.StringSerializer");
        kafkaConf.scheme = new KeyValueSchemeAsMultiScheme(new KafkaBoltKeyValueScheme());
        KafkaBolt bolt = new KafkaBolt()
                .withProducerProperties(props)
                .withTopicSelector(new DefaultTopicSelector(KAFKA_TOPIC_B))
                .withTupleToKafkaMapper(new FieldNameBasedTupleToKafkaMapper());

        TopologyBuilder topology = new TopologyBuilder();

        topology.setSpout("KafkaSpout", new KafkaSpout(kafkaConf), 4);

        topology.setBolt("PreProcessingBolt", new PreProcessingBolt(), 4)
                .shuffleGrouping("KafkaSpout");
        topology.setBolt("FeatureExtraction", new FeatureExtraction(), 4)
                .shuffleGrouping("PreProcessingBolt");
        topology.setBolt("MainFrameExtraction", new MainFrameExtraction(), 4)
                .shuffleGrouping("FeatureExtraction");
        topology.setBolt("forwardToKafka", new MainFrameExtraction(), 4)
                .shuffleGrouping("MainFrameExtraction");

        return topology.createTopology();
    }

    public static class KafkaBoltKeyValueScheme extends StringKeyValueScheme {
        @Override
        public Fields getOutputFields() {
            return new Fields("message");
        }
    }

    private static Config createConfig(boolean local)
    {
        int workers = 1;
        Config conf = new Config();
        conf.setDebug(true);
        if (local)
            conf.setMaxTaskParallelism(workers);
        else
            conf.setNumWorkers(workers);
        return conf;
    }
}
