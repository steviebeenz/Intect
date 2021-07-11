package net.square.intect.listener.bukkit;

import net.square.intect.Intect;
import net.square.intect.processor.data.PlayerStorage;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.player.PlayerQuitEvent;

public class PlayerUninjectListener implements Listener
{

    private final Intect intect;

    public PlayerUninjectListener(Intect intect)
    {
        this.intect = intect;
    }

    @EventHandler
    public void handle(PlayerQuitEvent event)
    {
        Player player = event.getPlayer();
        PlayerStorage.storageHashMap.remove(player);
        if(this.intect.getPacketManager().getPacketReceivor() != null) {
            this.intect.getPacketManager().getPacketReceivor().uninject(player);
        }
    }
}
