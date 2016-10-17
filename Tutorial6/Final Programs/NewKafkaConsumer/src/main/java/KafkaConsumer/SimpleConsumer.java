package KafkaConsumer;

import com.migcomponents.migbase64.Base64;
import kafka.consumer.Consumer;
import kafka.consumer.ConsumerConfig;
import kafka.consumer.ConsumerIterator;
import kafka.consumer.KafkaStream;
import kafka.javaapi.consumer.ConsumerConnector;

import java.io.*;
import java.util.*;

public class SimpleConsumer {
    private final ConsumerConnector consumer;
    private final String topic;
    int framecount;
    int textfile;

    public SimpleConsumer(String zookeeper, String groupId, String t) {
        Properties props = new Properties();
        props.put("zookeeper.connect", zookeeper);
        props.put("group.id", groupId);
        props.put("zookeeper.session.timeout.ms", "500");
        props.put("zookeeper.sync.time.ms", "250");
        props.put("auto.commit.interval.ms", "1000");

        framecount = 0;
        textfile = 0;
        consumer = Consumer.createJavaConsumerConnector(new ConsumerConfig(props));
        this.topic = t;
    }

    public void DecodeVideo(String encodedString){
        byte[] decodedBytes = Base64.decodeFast(encodedString.getBytes());

        try {
            FileOutputStream out = new FileOutputStream("decode.mkv");
            out.write(decodedBytes);
            out.close();
            System.out.println("Created File");

            // send data to mongo
            httprequest http = new httprequest();
            // generate random  video number
            Random rand = new Random();
            int n = rand.nextInt(100);
            String params = "{\"_id\":\"video#" + Integer.toBinaryString(n) +
                    "\",\"data\": \""+ encodedString +"\"}";
            http.sendData(params);
            System.out.println("Video sent to mongo");
        } catch (Exception e) {
            e.printStackTrace();

        }
    }

    public void DecodeFrames(String encodedString){
        byte[] decodedBytes = Base64.decodeFast(encodedString.getBytes());

        try {
            FileOutputStream out = new FileOutputStream("frame"+framecount+".jpg");
            out.write(decodedBytes);
            out.close();
            System.out.println("Created File");
            framecount++;
        } catch (Exception e) {
            e.printStackTrace();

        }
    }

    public void OutputText(String encodedString){
        try {
            FileOutputStream out = new FileOutputStream("text:"+textfile+".txt");
            PrintWriter pw = new PrintWriter(out);
            pw.write(encodedString);
            pw.close();
            out.close();
            System.out.println("Created File");
            textfile++;
        } catch (Exception e) {
            e.printStackTrace();

        }
    }

    public void testConsumer() {
        Map<String, Integer> topicCount = new HashMap<String, Integer>();
        topicCount.put(topic, 1);

        Map<String, List<KafkaStream<byte[], byte[]>>> consumerStreams = consumer.createMessageStreams(topicCount);
        List<KafkaStream<byte[], byte[]>> streams = consumerStreams.get(topic);
        StringBuilder sb = new StringBuilder();
        for (final KafkaStream stream : streams) {
            ConsumerIterator<byte[], byte[]> it = stream.iterator();
            int i=0;
            int t = 0;
            while (it.hasNext()) {
                try {
                    byte[] message = it.next().message();
                    String type = new String(message, "UTF-8");
                    if(type.equals("text")) {
                        t = 1;
                        sb = new StringBuilder();
                        continue;
                    }
                    else if (type.equals("frame")) {
                        t = 2;
                        sb = new StringBuilder();
                        continue;
                    }
                    else if (type.equals("video1")) {
                        t = 3;
                        sb = new StringBuilder();
                        continue;
                    }

                    String value = new String(message, "UTF-8");
                    sb.append(value);
                }
                catch(Exception e) {
                    e.printStackTrace();
                }

                i++;
                if(t == 1) {
                    OutputText(sb.toString());
                    i = 0;
                }
                else if(t == 2) {
                    DecodeFrames(sb.toString());
                    i = 0;
                }
                else if(i == 13 && t == 3){
                    System.out.println("Creating Video");
                    DecodeVideo(sb.toString());
                    i = 0;
                }
            }
        }
        if (consumer != null) {
            consumer.shutdown();
        }
    }

    public static void main(String[] args) {
        String t = args[0]; //Topic Name
        SimpleConsumer simpleConsumer = new SimpleConsumer("localhost:2181", "test-consumer-group", t);
        simpleConsumer.testConsumer();
    }

}