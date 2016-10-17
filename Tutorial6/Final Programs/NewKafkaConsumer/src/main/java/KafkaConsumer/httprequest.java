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

    public void sendData(String parameters) {
        try {
            URL url = new URL("https://api.mlab.com/api/1/databases/yeastdata" +
                    "/collections/test-collection?apiKey=rxNyrlJP9OVsJv6KPmB0lH1SnwYi438I");
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
