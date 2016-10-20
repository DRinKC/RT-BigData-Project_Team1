package KafkaConsumer;

/**
 * Created by brendan on 10/9/16.
 */

import java.io.BufferedReader;
import java.io.DataOutputStream;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.net.HttpURLConnection;
import java.net.URL;

public class httprequest {
    private HttpURLConnection connection = null;
    private URL url = null;

    httprequest(String urlstring) {
        try {
            url = new URL(urlstring);
        }
        catch(Exception e) {
            e.printStackTrace();
        }
    }

    public void sendData(String parameters) {
        try {
            connection = (HttpURLConnection) url.openConnection();
            connection.setRequestMethod("POST");
            connection.setRequestProperty("Content-Type", "application/json");
            connection.setDoOutput(true);

            //Send request
            DataOutputStream wr = new DataOutputStream (
                    connection.getOutputStream());
            wr.writeBytes(parameters);
            wr.close();

            //Get Response
            InputStream is = connection.getInputStream();
            BufferedReader rd = new BufferedReader(new InputStreamReader(is));
            StringBuilder response = new StringBuilder(); // or StringBuffer if Java version 5+
            String line;
            while ((line = rd.readLine()) != null) {
                response.append(line);
                response.append('\r');
            }
            rd.close();
            System.out.print(response.toString());
        }
        catch(Exception e) {
            e.printStackTrace();
        }
    }
}
