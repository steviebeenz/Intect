package net.square.intect.listener.bukkit;

import net.square.intect.Intect;
import net.square.intect.processor.manager.UpdateManager;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.player.PlayerJoinEvent;

import javax.swing.*;

public class PlayerJoinListener implements Listener {

    @EventHandler
    public void handle(PlayerJoinEvent event) {

        if (!event.getPlayer().hasPermission("intect.admin.update.notify")) {
            return;
        }

        int running = Integer.parseInt(Intect.getIntect().getDescription().getVersion());
        int latest = UpdateManager.getLatestBuild();

        if (running < latest) {
            event.getPlayer().sendMessage(
                Intect.getIntect().getPrefix() + "You`re §c" + (latest - running)
                    + " §7Intect build(s) out of date!\n§7Download: §chttps://jenkins.squarecode.de/job/Intect/job/master/");
        }
    }
}