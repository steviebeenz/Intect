package net.square.intect.checks.impl.killaura;

import io.github.retrooper.packetevents.packetwrappers.play.in.flying.WrappedPacketInFlying;
import io.github.retrooper.packetevents.packetwrappers.play.in.useentity.WrappedPacketInUseEntity;
import net.square.intect.checks.objectable.Check;
import net.square.intect.checks.objectable.CheckInfo;
import net.square.intect.utils.objectable.IntectPacket;
import net.square.intect.processor.data.PlayerStorage;

@CheckInfo(name = "Killaura", type = "F", description = "Checks for double hits", maxVL = 20)
public class KillauraTypeF extends Check
{

    public KillauraTypeF(PlayerStorage data)
    {
        super(data);
    }

    private long lastAura = 0;

    private int count = 0;

    @Override
    public void handle(IntectPacket packet)
    {

        if (shouldBypass()) return;

        if (packet.getRawPacket() instanceof WrappedPacketInUseEntity)
        {

            long delay = now() - lastAura;

            if (count > 2 && !getStorage().getCombatProcessor().getTarget()
                .equals(getStorage().getCombatProcessor().getLastTarget()) && delay < 10)
            {
                fail("attacked too many in a short time span", String.format("delay %d < 10", delay), 1);
            }
            count = count + 1;
            lastAura = now();
        }
        else if (packet.getRawPacket() instanceof WrappedPacketInFlying)
        {
            count = 0;
        }
    }
}
