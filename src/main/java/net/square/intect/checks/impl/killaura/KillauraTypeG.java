package net.square.intect.checks.impl.killaura;

import io.github.retrooper.packetevents.packetwrappers.play.in.useentity.WrappedPacketInUseEntity;
import net.square.intect.checks.objectable.Check;
import net.square.intect.checks.objectable.CheckInfo;
import net.square.intect.utils.objectable.IntectPacket;
import net.square.intect.processor.data.PlayerStorage;

@CheckInfo(name = "Killaura", type = "G", description = "Checks for open inventory", maxVL = 20)
public class KillauraTypeG extends Check
{

    public KillauraTypeG(PlayerStorage data)
    {
        super(data);
    }

    @Override
    public void handle(IntectPacket packet)
    {

        if (shouldBypass()) return;

        if (packet.getRawPacket() instanceof WrappedPacketInUseEntity)
        {
            if (getStorage().getActionProcessor().isInventory())
            {
                fail("attacked while in inventory", null, 1);
            }
        }
    }
}
