package net.square.intect.processor.receivor;

import org.bukkit.entity.Player;

public interface PacketReceivor {

    void inject(Player player);

    void uninject(Player player);
}
