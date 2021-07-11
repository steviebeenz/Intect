package net.square.intect.checks.impl.killaura;

import io.github.retrooper.packetevents.packetwrappers.play.in.flying.WrappedPacketInFlying;
import net.square.intect.checks.objectable.Check;
import net.square.intect.checks.objectable.CheckInfo;
import net.square.intect.utils.objectable.IntectPacket;
import net.square.intect.processor.custom.custom.WrappedPacketInArmAnimation;
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

        if (packet.getRawPacket() instanceof WrappedPacketInArmAnimation)
        {

            swings = swings + 1;

            if (swings >= 100)
            {
                if (hits >= 97 && increaseBuffer() > 1)
                {
                    fail("is attacking too accurate", "acc " + hits, 1);
                }
                else
                {
                    decreaseBufferBy(0.25);
                }
                swings = 0;
                hits = 0;
            }
        }
        else if (packet.getRawPacket() instanceof WrappedPacketInFlying)
        {
            hits = hits + 1;
        }
    }
}
