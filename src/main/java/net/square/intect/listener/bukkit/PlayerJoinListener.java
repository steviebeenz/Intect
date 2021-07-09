package net.square.intect.listener.bukkit;

import io.github.retrooper.packetevents.PacketEvents;
import net.square.intect.Intect;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.player.PlayerJoinEvent;

import java.util.logging.Level;

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

        Intect.getIntect()
            .getLogger()
            .log(Level.INFO,
                 "Client " + event.getPlayer().getName() + "/" + event.getPlayer().getUniqueId() + " connected with "
                     + PacketEvents.get().getPlayerUtils().getClientVersion(event.getPlayer()).name());

        if (running < latest && Intect.getIntect()
            .getConfigHandler()
            .getYamlConfiguration()
            .getBoolean("logging.alert-updates"))
        {
            event.getPlayer().sendMessage(
                Intect.getIntect().getPrefix() + "You`re §c" + (latest - running) + " §7build(s) out of date!");
            event.getPlayer().sendMessage("§chttps://jenkins.squarecode.de/job/Intect/job/master/");
        }
    }
}
