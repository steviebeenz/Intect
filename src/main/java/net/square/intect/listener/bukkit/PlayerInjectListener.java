package net.square.intect.listener.bukkit;

import net.square.intect.Intect;
import net.square.intect.processor.data.PlayerStorage;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.player.PlayerJoinEvent;

import java.util.logging.Level;

public class PlayerInjectListener implements Listener
{

    private final Intect intect;

    public PlayerInjectListener(Intect intect)
    {
        this.intect = intect;
    }

    @EventHandler
    public void handle(PlayerJoinEvent event)
    {
        Player player = event.getPlayer();
        PlayerStorage.storageHashMap.put(player, new PlayerStorage(player));
        if (this.intect.getPacketManager().getPacketReceivor() != null)
        {
            this.intect.getPacketManager().getPacketReceivor().inject(player);
        }
        else
        {
            player.kickPlayer(this.intect.getPrefix() + "§cNo packet-receivor available! Look console for details");
            this.intect.getLogger().log(Level.WARNING, "Operating version: " + this.intect.getRunningVersion());
            this.intect.getLogger().log(Level.WARNING, "Available versions: ");
            this.intect.getPacketManager()
                .getReceivorList()
                .forEach((s, packetReceivor) -> this.intect.getLogger()
                    .log(Level.WARNING, "Version: " + packetReceivor.getName()));
        }
    }
}
