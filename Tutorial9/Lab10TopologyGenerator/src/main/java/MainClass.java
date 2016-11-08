import dtParsing.Parsing;
import org.json.JSONObject;
import topology.CreateBolt;
import topology.CreateStormMainClass;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.net.URL;
import java.net.URLConnection;
import java.nio.charset.Charset;

/**
 * Created by Naga on 27-10-2016.
 */
public class    MainClass {

    public static void main(String args[]) throws IOException {
        String Path = "/home/brendan/IdeaProjects/Lab10TopologyGenerator/src/main/";
        String API_KEY = "rxNyrlJP9OVsJv6KPmB0lH1SnwYi438I";
        String DATABASE_NAME = "yeastdata";
        String COLLECTION_NAME = "spark-collection";
        String urlString = "https://api.mlab.com/api/1/databases/" +
        DATABASE_NAME + "/collections/" + COLLECTION_NAME + "?apiKey=" + API_KEY;


        /*
        Getting model from mongo DB
         */

        StringBuilder sb1 = new StringBuilder();
        InputStreamReader  in = null;
        URL url = new URL(urlString);
        URLConnection conn = url.openConnection();
        if (conn != null && conn.getInputStream() != null) {
            in = new InputStreamReader(conn.getInputStream(),
                    Charset.defaultCharset());
            InputStream is = conn.getInputStream();


            BufferedReader bufferedReader = new BufferedReader(in);
            if (bufferedReader != null) {
                int cp;
                while ((cp = bufferedReader.read()) != -1) {
                    sb1.append((char) cp);
                }
                bufferedReader.close();
            }
        }
        String adjusted = sb1.toString().subSequence(1,sb1.toString().length()-1).toString();
        JSONObject jsonObject = new JSONObject(adjusted);
        String model = jsonObject.get("Model").toString();
        System.out.println(model);

        System.out.println("Got Model from Mongo");

         /*
        From the input model, derive paths for each class
         */

        Parsing parsing1 = new Parsing(model, "data/Class1.txt", "0.0");
        Parsing parsing2 = new Parsing(model, "data/Class2.txt", "1.0");
        Parsing parsing3 = new Parsing(model, "data/Class3.txt", "2.0");
        Parsing parsing4 = new Parsing(model, "data/Class4.txt", "3.0");

        System.out.println("Derived Class Paths");
        /*
        Create Storm Topology
        Bolt for Each Path
         */

        String[] bolts = {"Class1", "Class2", "Class3", "Class4"};
        String[] classDTpaths = {"data/Class1.txt", "data/Class2.txt", "data/Class3.txt", "data/Class4.txt"};

        /*
        Create Bolt Class files
         */
        for(int i=0; i<bolts.length; i++){
            CreateBolt createBolt = new CreateBolt(Path, bolts[i], classDTpaths[i],
                    API_KEY, DATABASE_NAME, "featureout-collection");
        }

        /*
        Creating Storm Main Class
         */

        String spoutName = "kafka_spout_audioFeatures";
        StringBuilder sb = new StringBuilder();
        String spout = "        topology.setSpout(\"" + spoutName+ "\", new KafkaSpout(kafkaConf), 4);";
        sb.append(spout).append("\n");
        for(int i=0; i<bolts.length; i++){
            String boltName = bolts[i] + "Bolt";
            String s2 = "        topology.setBolt(\"" + bolts[i] + "\", new " +boltName +"(), 4).shuffleGrouping(\"" + spoutName+ "\");";
            sb.append(s2);
            sb.append("\n");
        }


        CreateStormMainClass createStormMainClass = new CreateStormMainClass(sb.toString(), Path);

        System.out.println("Created Storm Topology");
    }
}
