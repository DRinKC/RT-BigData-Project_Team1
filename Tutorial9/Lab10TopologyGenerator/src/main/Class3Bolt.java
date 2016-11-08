import org.apache.storm.topology.BasicOutputCollector;
import org.apache.storm.topology.OutputFieldsDeclarer;
import org.apache.storm.topology.base.BaseBasicBolt;
import org.apache.storm.tuple.Fields;
import org.apache.storm.tuple.Tuple;
import org.apache.storm.tuple.Values;
import org.json.JSONObject;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.*;
import java.net.HttpURLConnection;
import java.net.MalformedURLException;
import java.net.ProtocolException;
import java.net.URL;

public class Class3Bolt extends BaseBasicBolt {
    private static final Logger LOG = LoggerFactory.getLogger(Class3Bolt.class);
    public void execute(Tuple tuple, BasicOutputCollector basicOutputCollector) {
        try {
            String s = tuple.getString(0);

            double[] feature = fromString(s);
            Boolean check = checkClass3(feature);
            insertIntoMongoDB(check);
            basicOutputCollector.emit(new Values("empty",check));
        }
        catch (Exception e)
        {
            e.printStackTrace();
        }

    }

    public void declareOutputFields(OutputFieldsDeclarer outputFieldsDeclarer) {
        outputFieldsDeclarer.declare(new Fields("context","status"));
    }

    private static double[] fromString(String string) {
        String[] strings = string.split(" ");
        double result[] = new double[strings.length];
        for (int i = 0; i < result.length; i++) {
            result[i] = Double.parseDouble(strings[i]);
        }
        return result;
    }

    public static void insertIntoMongoDB(Boolean check) {
        String API_KEY = "rxNyrlJP9OVsJv6KPmB0lH1SnwYi438I";
        String DATABASE_NAME = "yeastdata";
        String COLLECTION_NAME = "featureout-collection";
        String urlString = "https://api.mlab.com/api/1/databases/" +
                DATABASE_NAME + "/collections/" + COLLECTION_NAME + "?apiKey=" + API_KEY;
        LOG.info(urlString);

        StringBuilder result = null;
        try {
            URL url = new URL(urlString);
            HttpURLConnection urlConnection = (HttpURLConnection) url.openConnection();
            urlConnection.setDoOutput(true);
            urlConnection.setRequestMethod("POST");
            urlConnection.setRequestProperty("Content-Type", "application/json");
            urlConnection.setRequestProperty("Accept", "application/json");
            Writer writer = new BufferedWriter(new OutputStreamWriter(urlConnection.getOutputStream(), "UTF-8"));
            JSONObject jsonObject = new JSONObject();
            jsonObject.put("Context", "Class3");
            jsonObject.put("Decision", check);
            jsonObject.put("Timestamp", System.currentTimeMillis());
            writer.write(jsonObject.toString());
            LOG.info(jsonObject.toString());
            writer.close();
            BufferedReader in = new BufferedReader(
                    new InputStreamReader(urlConnection.getInputStream()));
            String inputLine;
            StringBuffer response = new StringBuffer();

            while ((inputLine = in.readLine()) != null) {
                response.append(inputLine);
            }
            in.close();

        } catch (MalformedURLException e) {
            e.printStackTrace();
        } catch (ProtocolException e) {
            e.printStackTrace();
        } catch (IOException e) {
            e.printStackTrace();
        }

        System.out.println("Uploaded data to Mongo");

    }

    public Boolean checkClass3(double[] feature) {
        
  if (feature[48] <= 18.0)
   if (feature[81] <= -64.0)
    if (feature[23] <= -128.0)
     if (feature[69] <= -96.0)
      if (feature[59] <= -119.0)
       if (feature[19] <= -126.0)
        if (feature[41] <= -123.0)
         return false;
        else if (feature[41] > -123.0)
         if (feature[0] <= -114.0)
          if (feature[2] <= -128.0)
           return false;
          else if (feature[2] > -128.0)
           if (feature[0] <= -128.0)
            return false;
           else if (feature[0] > -128.0)
            return false;
         else if (feature[0] > -114.0)
          if (feature[0] <= -98.0)
           return false;
          else if (feature[0] > -98.0)
           return false;
       else if (feature[19] > -126.0)
        return false;
      else if (feature[59] > -119.0)
       return false;
     else if (feature[69] > -96.0)
      return false;
    else if (feature[23] > -128.0)
     if (feature[92] <= -79.0)
      if (feature[30] <= -123.0)
       if (feature[89] <= -89.0)
        if (feature[5] <= -94.0)
         if (feature[7] <= -37.0)
          if (feature[15] <= -43.0)
           if (feature[0] <= -125.0)
            return false;
           else if (feature[0] > -125.0)
            return false;
          else if (feature[15] > -43.0)
           return false;
         else if (feature[7] > -37.0)
          return false;
        else if (feature[5] > -94.0)
         return false;
       else if (feature[89] > -89.0)
        return false;
      else if (feature[30] > -123.0)
       if (feature[75] <= -128.0)
        return false;
       else if (feature[75] > -128.0)
        if (feature[25] <= -114.0)
         if (feature[5] <= -66.0)
          if (feature[24] <= -19.0)
           if (feature[1] <= -128.0)
            return false;
           else if (feature[1] > -128.0)
            if (feature[0] <= -122.0)
             if (feature[0] <= -128.0)
              return false;
             else if (feature[0] > -128.0)
              return false;
            else if (feature[0] > -122.0)
             return false;
          else if (feature[24] > -19.0)
           return false;
         else if (feature[5] > -66.0)
          return false;
        else if (feature[25] > -114.0)
         return false;
     else if (feature[92] > -79.0)
      if (feature[0] <= -112.0)
       if (feature[3] <= -120.0)
        return false;
       else if (feature[3] > -120.0)
        return false;
      else if (feature[0] > -112.0)
       return false;
   else if (feature[81] > -64.0)
    if (feature[71] <= -119.0)
     if (feature[7] <= -118.0)
      if (feature[0] <= -127.0)
       return false;
      else if (feature[0] > -127.0)
       return false;
     else if (feature[7] > -118.0)
      return false;
    else if (feature[71] > -119.0)
     return false;
  else if (feature[48] > 18.0)
   if (feature[68] <= -128.0)
    if (feature[73] <= -126.0)
     if (feature[4] <= -128.0)
      return false;
     else if (feature[4] > -128.0)
      return false;
    else if (feature[73] > -126.0)
     return false;
   else if (feature[68] > -128.0)
    return false;
    return false;
    }
}
