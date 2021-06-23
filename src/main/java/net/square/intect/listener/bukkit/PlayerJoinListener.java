package net.square.intect.listener.bukkit;

import net.square.intect.Intect;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.player.PlayerJoinEvent;

public class PlayerJoinListener implements Listener
{

    @EventHandler
    public void handle(PlayerJoinEvent event)
    {

        if (!event.getPlayer().hasPermission("intect.admin.update.notify"))
        {
            return;
        }

        int running = Integer.parseInt(Intect.getIntect().getDescription().getVersion());
        int latest = Intect.getIntect().getUpdateManager().getLatestBuild();

        if (running < latest)
        {
            event.getPlayer().sendMessage(
                Intect.getIntect().getPrefix() + "You`re §9" + (latest - running) + " §7Intect build(s) out of date!");
            event.getPlayer().sendMessage("§9https://jenkins.squarecode.de/job/Intect/job/master/");
        }
    }
}
