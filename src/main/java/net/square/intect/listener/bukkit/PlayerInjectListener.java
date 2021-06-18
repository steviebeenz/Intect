package net.square.intect.listener.bukkit;

import net.square.intect.Intect;
import net.square.intect.processor.data.PlayerStorage;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.player.PlayerJoinEvent;

public class PlayerInjectListener implements Listener {

    private final Intect intect;

    public PlayerInjectListener(Intect intect) {
        this.intect = intect;
    }

    @EventHandler
    public void handle(PlayerJoinEvent event) {
        long now = System.currentTimeMillis();

        Player player = event.getPlayer();
        PlayerStorage.storageHashMap.put(player, new PlayerStorage(player));
        this.intect.getPacketManager().getPacketReceivor().inject(player);

        player.sendMessage(
            String.format(
                "%s§7Successfully inject storage after §a%dms", Intect.getIntect().getPrefix(),
                System.currentTimeMillis()
                    - now
            ));
    }
}
