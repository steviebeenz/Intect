package net.square.intect.utils.paster;

import javax.net.ssl.HttpsURLConnection;
import java.io.BufferedReader;
import java.io.DataOutputStream;
import java.io.IOException;
import java.io.InputStreamReader;
import java.net.URL;
import java.nio.charset.StandardCharsets;

public class Hastebin
{
    public static String post(String requestURL, String text)
    {
        try
        {
            byte[] postData = text.getBytes(StandardCharsets.UTF_8);
            int postDataLength = postData.length;

            URL url = new URL(requestURL);
            HttpsURLConnection conn = (HttpsURLConnection) url.openConnection();
            conn.setDoOutput(true);
            conn.setInstanceFollowRedirects(false);
            conn.setRequestMethod("POST");
            conn.setRequestProperty("User-Agent", "Hastebin Java Api");
            conn.setRequestProperty("Content-Length", Integer.toString(postDataLength));
            conn.setUseCaches(false);

            String response;
            DataOutputStream wr;

            try
            {
                wr = new DataOutputStream(conn.getOutputStream());
                wr.write(postData);
                BufferedReader reader = new BufferedReader(new InputStreamReader(conn.getInputStream()));
                response = reader.readLine();

            } catch (IOException e)
            {
                return "NULL";
            }

            if (response == null)
            {
                return "NULL";
            }

            if (response.contains("\"key\""))
            {
                response = response.substring(response.indexOf(":") + 2, response.length() - 2);
                response = requestURL.replace("/documents", "") + "/" + response;
            }

            return response;
        } catch (IOException exception)
        {
            return "NULL";
        }
    }
}
