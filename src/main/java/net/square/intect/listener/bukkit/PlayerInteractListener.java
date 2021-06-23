package net.square.intect.listener.bukkit;

import net.square.intect.processor.data.PlayerStorage;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.block.BlockBreakEvent;
import org.bukkit.event.block.BlockPlaceEvent;
import org.bukkit.event.player.PlayerInteractEvent;

public class PlayerInteractListener implements Listener
{

    @EventHandler
    public void onBlockBreak(final BlockBreakEvent event)
    {
        final PlayerStorage data = PlayerStorage.storageHashMap.get(event.getPlayer());
        if (data != null)
        {
            data.getActionProcessor().handleBukkitBlockBreak();
        }
    }

    @EventHandler
    public void onPlayerInteract(final PlayerInteractEvent event)
    {
        final PlayerStorage data = PlayerStorage.storageHashMap.get(event.getPlayer());
        if (data != null)
        {
            data.getActionProcessor().handleInteract(event);
        }
    }

    @EventHandler
    public void onBlockPlace(final BlockPlaceEvent event)
    {
        final PlayerStorage data = PlayerStorage.storageHashMap.get(event.getPlayer());
        if (data != null)
        {
            data.getActionProcessor().handleBukkitPlace();
        }
    }
}
