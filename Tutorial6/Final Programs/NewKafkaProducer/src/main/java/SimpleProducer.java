//package Tutorial6;

import com.google.common.base.Splitter;
import com.google.common.collect.Iterables;
import com.migcomponents.migbase64.Base64;
import kafka.javaapi.producer.Producer;
import kafka.producer.KeyedMessage;
import kafka.producer.ProducerConfig;

import java.io.*;
import java.nio.charset.Charset;
import java.util.Properties;

public class SimpleProducer {
    private static Producer<Integer, String> producer;
    private final Properties properties = new Properties();

    public SimpleProducer() {
        properties.put("metadata.broker.list", "localhost:9092");
        properties.put("serializer.class", "kafka.serializer.StringEncoder");
        properties.put("request.required.acks", "1");
        properties.put("message.max.bytes", "10000000");
        producer = new Producer<Integer, String>(new ProducerConfig(properties));

    }

    public static void EncodeVideo(String file, String topic){
        String encodedString = null;
        InputStream inputStream = null;
        try {
            inputStream = new FileInputStream(file);
        } catch (Exception e) {
            // TODO: handle exception
        }
        byte[] bytes;
        byte[] buffer = new byte[8192];
        int bytesRead;
        ByteArrayOutputStream output = new ByteArrayOutputStream();
        try {
            while ((bytesRead = inputStream.read(buffer)) != -1) {
                output.write(buffer, 0, bytesRead);
            }
        } catch (IOException e) {
            e.printStackTrace();
        }
        bytes = output.toByteArray();
        encodedString = Base64.encodeToString(bytes, true);

        Iterable<String> result = Splitter.fixedLength(100000).split(encodedString); //Splitting the video file
        String[] parts = Iterables.toArray(result, String.class); //Parts of video file
        KeyedMessage<Integer, String> type = new KeyedMessage<Integer, String>(topic, "video1");
        producer.send(type);
        for(int i=0; i<parts.length; i++){
            KeyedMessage<Integer, String> data = new KeyedMessage<Integer, String>(topic, parts[i]);
            System.out.println(parts[i]);
            producer.send(data);
        }
        System.out.println("Video Sent");
    }

    public static void SendFrames(String file, String topic){
        String encodedString = null;
        InputStream inputStream = null;
        File[] files = new File(file).listFiles();

        for(File f : files) {
            try {
                inputStream = new FileInputStream(f);
            } catch (Exception e) {
                e.printStackTrace();
            }
            byte[] bytes;
            byte[] buffer = new byte[8192];
            int bytesRead;
            ByteArrayOutputStream output = new ByteArrayOutputStream();
            try {
                while ((bytesRead = inputStream.read(buffer)) != -1) {
                    output.write(buffer, 0, bytesRead);
                }
            } catch (IOException e) {
                e.printStackTrace();
            }
            bytes = output.toByteArray();
            encodedString = Base64.encodeToString(bytes, true);

            KeyedMessage<Integer, String> type = new KeyedMessage<Integer, String>(topic, "frame");
            producer.send(type);

            Iterable<String> result = Splitter.fixedLength(100000).split(encodedString); //Splitting the video file
            String[] parts = Iterables.toArray(result, String.class); //Parts of video file
            int i = 0;
            KeyedMessage<Integer, String> data = new KeyedMessage<Integer, String>(topic, parts[i]);
            System.out.println(parts[i]);
            producer.send(data);
            System.out.println("Image Sent");
        }
    }

    public static void SendText(String file, String topic){
        String encodedString = null;
        BufferedReader br = null;
        File f = new File(file);

        try {
            br = new BufferedReader(new FileReader(f));
            String line;
            StringBuffer sb = new StringBuffer();

            while((line = br.readLine()) != null) {
                sb.append(line);
            }

            KeyedMessage<Integer, String> type = new KeyedMessage<Integer, String>(topic, "text");
            producer.send(type);

            Iterable<String> result = Splitter.fixedLength(100000).split(sb.toString()); //Splitting the video file
            String[] parts = Iterables.toArray(result, String.class); //Parts of video file
            int i = 0;
            KeyedMessage<Integer, String> data = new KeyedMessage<Integer, String>(topic, parts[i]);
            System.out.println(data.message());
            producer.send(data);
            System.out.println("File Sent");
            br.close();

        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    public static void main(String args[]) {
        SimpleProducer sp = new SimpleProducer();
        String topic = "Group01Topic";
        SendFrames("output/realFrames", topic);
        sp.SendText("output/features.txt",topic);
        sp.SendText("output/metadata.txt",topic);
        producer.close();
    }
}


