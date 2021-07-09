package net.square.intect.processor.manager;

import com.google.gson.JsonObject;
import com.google.gson.JsonParser;
import net.square.intect.Intect;

import java.io.*;
import java.net.URL;
import java.nio.charset.StandardCharsets;

public class UpdateManager
{

    private int latestBuild = -1;

    private String readAll(Reader rd) throws IOException
    {
        StringBuilder sb = new StringBuilder();
        int cp;
        while ((cp = rd.read()) != -1)
        {
            sb.append((char) cp);
        }
        return sb.toString();
    }

    public JsonObject readJsonFromUrl(String url) throws IOException
    {
        try (InputStream is = new URL(url).openStream())
        {
            BufferedReader rd = new BufferedReader(new InputStreamReader(is, StandardCharsets.UTF_8));
            String jsonText = readAll(rd);
            return new JsonParser().parse(jsonText).getAsJsonObject();
        }
    }

    public int getLatestBuild()
    {
        return latestBuild;
    }

    public void performCheck()
    {
        try
        {
            latestBuild = readJsonFromUrl("https://jenkins.squarecode.de/job/Intect/job/master/api/json").get(
                "lastSuccessfulBuild").getAsJsonObject().get("number").getAsInt();
        } catch (IOException e)
        {
            latestBuild = -1;
        }
    }

    public void init()
    {
        performCheck();
        Intect.getIntect()
            .getServer()
            .getScheduler()
            .runTaskTimerAsynchronously(Intect.getIntect(), this::performCheck, 200, 200);
    }
}
