package net.square.intect.checks.checks.killaura;

import net.minecraft.server.v1_8_R3.PacketPlayInArmAnimation;
import net.minecraft.server.v1_8_R3.PacketPlayInUseEntity;
import net.square.intect.checks.objectable.Check;
import net.square.intect.checks.objectable.CheckInfo;
import net.square.intect.checks.objectable.IntectPacket;
import net.square.intect.processor.data.PlayerStorage;

@CheckInfo(name = "Killaura", type = "D", description = "Checks for hit miss ratio", maxVL = 20)
public class KillauraTypeD extends Check
{

    public KillauraTypeD(PlayerStorage data)
    {
        super(data);
    }

    private int swings = 0;
    private int hits = 0;

    @Override
    public void handle(IntectPacket packet)
    {

        if (shouldBypass()) return;

        if (packet.getRawPacket() instanceof PacketPlayInArmAnimation)
        {

            swings = swings + 1;

            if (swings >= 100)
            {
                if (hits >= 97)
                {
                    fail();
                }
                swings = 0;
                hits = 0;
            }
        }
        else if (packet.getRawPacket() instanceof PacketPlayInUseEntity)
        {
            hits = hits + 1;
        }
    }
}
