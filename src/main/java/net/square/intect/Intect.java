package net.square.intect;

import io.github.retrooper.packetevents.PacketEvents;
import lombok.Getter;
import net.square.intect.commands.IntectCommand;
import net.square.intect.commands.LogsCommand;
import net.square.intect.handler.config.ConfigHandler;
import net.square.intect.handler.database.MySQLManager;
import net.square.intect.listener.bukkit.*;
import net.square.intect.processor.manager.*;
import net.square.intect.utils.metrics.Metrics;
import org.bukkit.plugin.java.JavaPlugin;

import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.logging.Level;

public final class Intect extends JavaPlugin
{

    @Getter
    private PacketManager packetManager;
    @Getter
    private StorageManager storageManager;
    @Getter
    private MySQLManager mySQLManager;
    @Getter
    private ConfigHandler configHandler;
    @Getter
    private ExecutorService service = Executors.newCachedThreadPool();
    @Getter
    private final TickManager tickManager = new TickManager();
    @Getter
    private final UpdateManager updateManager = new UpdateManager();
    @Getter
    private static Intect intect;
    @Getter
    private final String prefix = "§9§lIntect §8> §7";

    @Override
    public void onLoad()
    {
        this.configHandler = new ConfigHandler(this);
    }

    @Override
    public void onEnable()
    {
        // Plugin startup logic

        final double now = System.currentTimeMillis();

        this.getLogger().log(Level.INFO, "Trying to start intect...");

        intect = this;

        packetManager = new PacketManager(this);
        storageManager = new StorageManager();
        this.mySQLManager = new MySQLManager(this.configHandler.getYamlConfiguration().getString("mysql.address"),
                                             this.configHandler.getYamlConfiguration().getString("mysql.database"),
                                             this.configHandler.getYamlConfiguration().getString("mysql.username"),
                                             this.configHandler.getYamlConfiguration().getString("mysql.password"),
                                             this.configHandler.getYamlConfiguration().getInt("mysql.port"),
                                             this.configHandler.getYamlConfiguration().getString("mysql.prefix"));
        updateManager.init();
        ModuleManager.setup();

        PacketEvents.create(this).load();

        new Metrics(this, 11518);

        this.getServer().getPluginManager().registerEvents(new PlayerInjectListener(this), this);
        this.getServer().getPluginManager().registerEvents(new PlayerUninjectListener(this), this);
        this.getServer().getPluginManager().registerEvents(new PlayerInteractListener(), this);
        this.getServer().getPluginManager().registerEvents(new PlayerInventoryListener(), this);
        this.getServer().getPluginManager().registerEvents(new PlayerJoinListener(), this);

        this.getCommand("intect").setExecutor(new IntectCommand(this));
        this.getCommand("logs").setExecutor(new LogsCommand());

        tickManager.start();

        packetManager.setupReceivor();
        packetManager.initPacketHandler();

        this.getLogger()
            .log(Level.INFO, "Plugin started successfully after " + (System.currentTimeMillis() - now) + "ms");
    }

    @Override
    public void onDisable()
    {

        // Plugin shutdown logic

        tickManager.stop();
        mySQLManager.close();
    }

    /**
     * Return server version based on the running environment package name
     *
     * @return String running version
     */
    public String getRunningVersion()
    {
        String a = getServer().getClass().getPackage().getName();
        return a.substring(a.lastIndexOf('.') + 1);
    }
}
