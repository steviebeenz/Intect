package net.square.intect.checks.impl.heuristics;

import io.github.retrooper.packetevents.packetwrappers.play.in.flying.WrappedPacketInFlying;
import net.square.intect.checks.objectable.Check;
import net.square.intect.checks.objectable.CheckInfo;
import net.square.intect.utils.objectable.IntectPacket;
import net.square.intect.processor.data.PlayerStorage;

@CheckInfo(name = "Heuristics", type = "G", description = "Checks for invalid rotations", maxVL = 20)
public class HeuristicsTypeG extends Check
{

    public HeuristicsTypeG(PlayerStorage data)
    {
        super(data);
    }

    private double space = 0;

    @Override
    public void handle(IntectPacket packet)
    {

        if (shouldBypass()) return;

        if (packet.getRawPacket() instanceof WrappedPacketInFlying)
        {

            if(((WrappedPacketInFlying) packet.getRawPacket()).isLook()) {
                final float deltaYaw = getStorage().getRotationProcessor().getDeltaYaw();
                final float lastDeltaYaw = getStorage().getRotationProcessor().getLastDeltaYaw();
                final float lastLastDeltaYaw = (float) space;

                if (deltaYaw < 5F && lastDeltaYaw > 20F && lastLastDeltaYaw < 5F)
                {
                    if (increaseBuffer() > 3)
                    {
                        fail("rotated invalid", "dY < 5, lDY > 20", 1);
                    }
                }
                else
                {
                    decreaseBufferBy(0.75);
                }
                space = lastDeltaYaw;
            }
        }
    }
}
