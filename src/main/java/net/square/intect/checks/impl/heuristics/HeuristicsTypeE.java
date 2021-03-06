package net.square.intect.checks.impl.heuristics;

import io.github.retrooper.packetevents.packetwrappers.play.in.flying.WrappedPacketInFlying;
import net.square.intect.checks.objectable.Check;
import net.square.intect.checks.objectable.CheckInfo;
import net.square.intect.utils.objectable.IntectPacket;
import net.square.intect.processor.data.PlayerStorage;

@CheckInfo(name = "Heuristics", type = "E", description = "Checks for provocative movement", maxVL = 20)
public class HeuristicsTypeE extends Check
{

    public HeuristicsTypeE(PlayerStorage data)
    {
        super(data);
    }

    @Override
    public void handle(IntectPacket packet)
    {

        if (shouldBypass()) return;

        if (packet.getRawPacket() instanceof WrappedPacketInFlying)
        {
            final float pitch = getStorage().getRotationProcessor().getPitch();
            final float deltaYaw =
                getStorage().getRotationProcessor().getYaw() - getStorage().getRotationProcessor().getLastYaw();
            final float deltaPitch = getStorage().getRotationProcessor().getDeltaPitch();

            final boolean invalidPitch = deltaPitch < 0.009 && validRotation(deltaYaw);
            final boolean invalidYaw = deltaYaw < 0.009 && validRotation(deltaPitch);

            final boolean invalid = (invalidPitch || invalidYaw) && pitch < 89f;

            if (invalid)
            {
                if (increaseBuffer() > 20)
                {
                    fail("rotated invalid", "iP | iY & pt < 89", 1);
                }
            }
            else
            {
                decreaseBufferBy(2);
            }
        }
    }

    private boolean validRotation(float rotation)
    {
        return rotation > 2F && rotation < 35F;
    }
}
