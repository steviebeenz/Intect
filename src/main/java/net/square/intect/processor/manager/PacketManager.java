package net.square.intect.processor.manager;

import com.google.common.collect.Maps;
import lombok.Getter;
import net.square.intect.Intect;
import net.square.intect.processor.packet.v1_10_R1.ReceivorV1_10_R1;
import net.square.intect.processor.packet.v1_11_R1.ReceivorV1_11_R1;
import net.square.intect.processor.packet.v1_12_R1.ReceivorV1_12_R1;
import net.square.intect.processor.packet.v1_13_R2.ReceivorV1_13_R2;
import net.square.intect.processor.packet.v1_14_R1.ReceivorV1_14_R1;
import net.square.intect.processor.packet.v1_15_R1.ReceivorV1_15_R1;
import net.square.intect.processor.packet.v1_16_R3.ReceivorV1_16_R3;
import net.square.intect.processor.packet.v1_8_R3.ReceivorV1_8_R3;
import net.square.intect.processor.packet.v1_9_R2.ReceivorV1_9_R2;
import net.square.intect.processor.receivor.PacketReceivor;

import java.util.HashMap;
import java.util.logging.Level;

public class PacketManager
{

    @Getter
    private final HashMap<String, PacketReceivor> receivorList = Maps.newHashMap();
    private final Intect intect;
    @Getter
    private PacketReceivor packetReceivor;

    public PacketManager(Intect intect)
    {
        this.intect = intect;
    }

    public void setupReceivor()
    {
        this.receivorList.put("v1_8_R3", new ReceivorV1_8_R3(this.intect));
        this.receivorList.put("v1_9_R2", new ReceivorV1_9_R2(this.intect));
        this.receivorList.put("v1_10_R1", new ReceivorV1_10_R1(this.intect));
        this.receivorList.put("v1_11_R1", new ReceivorV1_11_R1(this.intect));
        this.receivorList.put("v1_12_R1", new ReceivorV1_12_R1(this.intect));
        this.receivorList.put("v1_13_R2", new ReceivorV1_13_R2(this.intect));
        this.receivorList.put("v1_14_R1", new ReceivorV1_14_R1(this.intect));
        this.receivorList.put("v1_15_R1", new ReceivorV1_15_R1(this.intect));
        this.receivorList.put("v1_16_R3", new ReceivorV1_16_R3(this.intect));

        Intect.getIntect().getLogger().log(Level.INFO, String.format("Implemented %d receivers!", receivorList.size()));
    }

    public void initPacketHandler()
    {
        String runningVersion = intect.getRunningVersion();
        this.intect.getLogger().log(Level.INFO, "Found running version: " + runningVersion);

        if (receivorList.containsKey(runningVersion))
        {
            this.packetReceivor = receivorList.get(runningVersion);
            this.intect.getLogger().log(Level.INFO, "Using adapter: " + runningVersion);
        }
        else
        {
            this.intect.getLogger().log(Level.INFO, "Cant find any good receivor for version " + runningVersion + "!");
        }
    }
}
