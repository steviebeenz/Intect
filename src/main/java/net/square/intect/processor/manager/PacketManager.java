package net.square.intect.processor.manager;

import com.google.common.collect.Maps;
import lombok.Getter;
import net.square.intect.Intect;
import net.square.intect.processor.receivor.PacketReceivor;
import net.square.intect.processor.packet.v1_8_R3.ReceivorV1_8_R3;
import org.bukkit.entity.Player;

import java.util.HashMap;
import java.util.logging.Level;

public class PacketManager {

    private final HashMap<String, PacketReceivor> receivorList = Maps.newHashMap();
    private final Intect intect;
    @Getter
    private PacketReceivor packetReceivor;

    public PacketManager(Intect intect) {
        this.intect = intect;
    }

    public void setupReceivor() {
        this.receivorList.put("v1_8_R3", new ReceivorV1_8_R3(this.intect));
    }

    public void initPacketHandler() {
        String runningVersion = intect.getRunningVersion();
        this.intect.getLogger().log(Level.INFO, "Found running version: " + runningVersion);

        if (receivorList.containsKey(runningVersion)) {
            this.packetReceivor = receivorList.get(runningVersion);
            this.intect.getLogger().log(Level.INFO, "Using adapter " + runningVersion);
        }
    }
}
