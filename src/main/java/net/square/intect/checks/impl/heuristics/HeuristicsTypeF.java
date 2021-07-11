package net.square.intect.checks.impl.heuristics;

import io.github.retrooper.packetevents.packetwrappers.play.in.flying.WrappedPacketInFlying;
import net.square.intect.checks.objectable.Check;
import net.square.intect.checks.objectable.CheckInfo;
import net.square.intect.utils.objectable.IntectPacket;
import net.square.intect.processor.data.PlayerStorage;

@CheckInfo(name = "Heuristics", type = "F", description = "Checks for pattern movement", maxVL = 20)
public class HeuristicsTypeF extends Check
{

    public HeuristicsTypeF(PlayerStorage data)
    {
        super(data);
    }

    @Override
    public void handle(IntectPacket packet)
    {

        if (shouldBypass()) return;

        if (packet.getRawPacket() instanceof WrappedPacketInFlying)
        {
            final float customFloat = getStorage().getRotationProcessor().getDeltaYaw();
            final float deltaPitch = getStorage().getRotationProcessor().getDeltaPitch();

            final boolean invalid = (deltaPitch % 1 == 0 || customFloat % 1 == 0)
                && deltaPitch != 0 && customFloat != 0;

            if (invalid)
            {
                if (increaseBuffer() > 3)
                {
                    fail("rotated invalid", "dP % 1, cF % 1, dp != 0, cF != 0", 1);
                }
            }
            else
            {
                decreaseBufferBy(0.5);
            }
        }
    }
}
