package net.square.intect;

import com.google.common.collect.Lists;
import io.github.retrooper.packetevents.PacketEvents;
import lombok.Getter;
import net.square.intect.commands.IntectCommand;
import net.square.intect.listener.bukkit.BukkitInteractListener;
import net.square.intect.listener.bukkit.PlayerInjectListener;
import net.square.intect.listener.bukkit.PlayerUninjectListener;
import net.square.intect.processor.manager.ModuleManager;
import net.square.intect.processor.manager.PacketManager;
import net.square.intect.processor.manager.TickManager;
import org.bukkit.entity.Player;
import org.bukkit.plugin.java.JavaPlugin;

import java.util.List;
import java.util.logging.Level;

public final class Intect extends JavaPlugin {

    @Getter
    private final List<Player> verboseMode = Lists.newArrayList();

    @Getter
    private PacketManager packetManager;
    @Getter
    private final TickManager tickManager = new TickManager();
    @Getter
    private static Intect intect;
    @Getter
    private final String prefix = "§8[§c§lIntect§8] §7";

    @Override
    public void onEnable() {

        // Plugin startup logic
        final double now = System.currentTimeMillis();
        intect = this;

        packetManager = new PacketManager(this);
        ModuleManager.setup();

        PacketEvents.create(this).load();

        this.getServer().getPluginManager().registerEvents(new PlayerInjectListener(this), this);
        this.getServer().getPluginManager().registerEvents(new PlayerUninjectListener(this), this);
        this.getServer().getPluginManager().registerEvents(new BukkitInteractListener(), this);

        this.getCommand("intect").setExecutor(new IntectCommand(this));

        tickManager.start();

        packetManager.setupReceivor();
        packetManager.initPacketHandler();

        this.getLogger()
            .log(Level.INFO, "Plugin started successfully after " + (System.currentTimeMillis() - now) + "ms");
    }

    @Override
    public void onDisable() {

        // Plugin shutdown logic

        tickManager.stop();
    }

    public String getRunningVersion() {
        String a = getServer().getClass().getPackage().getName();
        return a.substring(a.lastIndexOf('.') + 1);
    }
}