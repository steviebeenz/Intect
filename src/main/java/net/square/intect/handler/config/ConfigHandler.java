package net.square.intect.handler.config;

import lombok.Getter;
import net.square.intect.Intect;
import org.bukkit.configuration.file.YamlConfiguration;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.InputStreamReader;
import java.nio.charset.StandardCharsets;

public class ConfigHandler
{
    private final Intect intect;
    @Getter
    private YamlConfiguration yamlConfiguration;

    public ConfigHandler(Intect intect)
    {
        this.intect = intect;
        loadFile();
    }

    public void loadFile()
    {
        File file = new File("plugins/" + intect.getDescription().getName() + "/config.yml");

        if (!file.exists())
        {
            intect.saveResource("config.yml", false);
        }
        try
        {
            this.yamlConfiguration = YamlConfiguration.loadConfiguration(
                new InputStreamReader(new FileInputStream(file), StandardCharsets.UTF_8)
            );
        } catch (FileNotFoundException e)
        {
            e.printStackTrace();
        }
    }
}
