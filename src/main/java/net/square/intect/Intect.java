package net.square.intect;

import io.github.retrooper.packetevents.PacketEvents;
import lombok.Getter;
import net.square.intect.commands.IntectCommand;
import net.square.intect.handler.config.ConfigHandler;
import net.square.intect.listener.bukkit.*;
import net.square.intect.processor.manager.*;
import net.square.intect.utils.metrics.Metrics;
import net.square.intect.utils.verify.IntectLocalVerify;
import org.bukkit.plugin.java.JavaPlugin;

import java.util.logging.Level;

public final class Intect extends JavaPlugin
{

    @Getter
    private PacketManager packetManager;
    @Getter
    private StorageManager storageManager;
    @Getter
    private ConfigHandler configHandler;
    @Getter
    private final TickManager tickManager = new TickManager();
    @Getter
    private final UpdateManager updateManager = new UpdateManager();
    @Getter
    private IntectLocalVerify intectLocalVerify;
    @Getter
    private static Intect intect;
    @Getter
    private final String prefix = "§8[§c§lIntect§8] §7";
    @Getter
    private String[] state;

    @Override
    public void onLoad()
    {
        this.configHandler = new ConfigHandler(this);
        this.intectLocalVerify = new IntectLocalVerify(this,
                                                       "E8YJP-00OOQ-VOFWP-3KCJW-8242W",
                                                       "https://dash.squarecode.de/api/client",
                                                       "4994153f292d7804c4173ab06515bca924659c4c");
        this.state = intectLocalVerify.isValid();
    }

    @Override
    public void onEnable()
    {
        // Plugin startup logic

        // Todo: redesign the whole anticheat like intave

        final double now = System.currentTimeMillis();

        if (!state[0].equalsIgnoreCase("3") && !state[0].equalsIgnoreCase("2"))
        {
            this.getLogger().log(Level.WARNING,
                                 String.format("Invalid license! Shutdown... (%s/%s)", state[0], state[1]));
            this.getPluginLoader().disablePlugin(this);
            return;
        }

        this.getLogger().log(Level.INFO, "Trying to start intect...");

        this.getLogger().log(Level.INFO, String.format("Licensed for: %s", state[4]));
        
        intect = this;

        packetManager = new PacketManager(this);
        storageManager = new StorageManager();

        updateManager.init();
        ModuleManager.setup();

        PacketEvents.create(this).load();
        PacketEvents.get().loadAsyncNewThread();

        new Metrics(this, 11518);

        this.getServer().getPluginManager().registerEvents(new PlayerInjectListener(this), this);
        this.getServer().getPluginManager().registerEvents(new PlayerUninjectListener(this), this);
        this.getServer().getPluginManager().registerEvents(new PlayerInteractListener(), this);
        this.getServer().getPluginManager().registerEvents(new PlayerJoinListener(), this);

        this.getCommand("intect").setExecutor(new IntectCommand(this));

        tickManager.start();

        packetManager.setupReceivor();
        packetManager.initPacketHandler();

        this.getLogger()
            .log(Level.INFO, "Intect booted successful after " + (System.currentTimeMillis() - now) + "ms");
    }

    @Override
    public void onDisable()
    {

        // Plugin shutdown logic

        tickManager.stop();
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
