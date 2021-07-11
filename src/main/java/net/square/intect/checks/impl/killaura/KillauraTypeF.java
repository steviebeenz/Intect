package net.square.intect.checks.impl.killaura;

import io.github.retrooper.packetevents.packetwrappers.play.in.useentity.WrappedPacketInUseEntity;
import net.square.intect.checks.objectable.Check;
import net.square.intect.checks.objectable.CheckInfo;
import net.square.intect.processor.data.PlayerStorage;
import net.square.intect.utils.objectable.IntectPacket;

@CheckInfo(name = "Killaura", type = "F", description = "Checks for double hits", maxVL = 20)
public class KillauraTypeF extends Check
{

    public KillauraTypeF(PlayerStorage data)
    {
        super(data);
    }

    @Override
    public void handle(IntectPacket packet)
    {

        if (shouldBypass()) return;

        if (packet.getRawPacket() instanceof WrappedPacketInUseEntity)
        {
            int targets = getStorage().getCombatProcessor().getCurrentTargets();

            if (targets > 1)
            {
                fail("attacked too many in a short time", String.format("%d targets", targets), 1);
            }
        }
    }
}
