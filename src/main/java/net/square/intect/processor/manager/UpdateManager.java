package net.square.intect.processor.manager;

import com.google.gson.JsonObject;
import com.google.gson.JsonParser;
import net.square.intect.Intect;

import java.io.*;
import java.net.URL;
import java.nio.charset.StandardCharsets;

public class UpdateManager
{

    private static int latestBuild = -1;

    private static String readAll(Reader rd) throws IOException
    {
        StringBuilder sb = new StringBuilder();
        int cp;
        while ((cp = rd.read()) != -1)
        {
            sb.append((char) cp);
        }
        return sb.toString();
    }

    private static JsonObject readJsonFromUrl(String url) throws IOException
    {
        InputStream is = new URL(url).openStream();
        try
        {
            BufferedReader rd = new BufferedReader(new InputStreamReader(is, StandardCharsets.UTF_8));
            String jsonText = readAll(rd);
            JsonObject json = new JsonParser().parse(jsonText).getAsJsonObject();
            return json;
        } finally
        {
            is.close();
        }
    }

    public static int getLatestBuild()
    {
        return latestBuild;
    }

    public static void init()
    {
        try
        {
            latestBuild = readJsonFromUrl("https://jenkins.squarecode.de/job/Intect/job/master/api/json").get(
                "lastSuccessfulBuild").getAsJsonObject().get("number").getAsInt();
        } catch (IOException e)
        {
            latestBuild = -1;
        }
        Intect.getIntect().getServer().getScheduler().runTaskTimerAsynchronously(Intect.getIntect(), () ->
        {
            try
            {
                latestBuild = readJsonFromUrl("https://jenkins.squarecode.de/job/Intect/job/master/api/json").get(
                    "lastSuccessfulBuild").getAsJsonObject().get("number").getAsInt();
            } catch (IOException e)
            {
                latestBuild = -1;
            }
        }, 100, 100);
    }
}
