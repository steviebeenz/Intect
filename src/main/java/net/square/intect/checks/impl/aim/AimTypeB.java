package net.square.intect.checks.impl.aim;

import io.github.retrooper.packetevents.packetwrappers.play.in.useentity.WrappedPacketInUseEntity;
import net.square.intect.checks.objectable.Check;
import net.square.intect.checks.objectable.CheckInfo;
import net.square.intect.utils.objectable.IntectPacket;
import net.square.intect.processor.data.PlayerStorage;

@CheckInfo(name = "Aim", type = "B", description = "Rounded flaw", maxVL = 20)
public class AimTypeB extends Check
{
    public AimTypeB(PlayerStorage data)
    {
        super(data);
    }

    @Override
    public void handle(IntectPacket packet)
    {

        if (shouldBypass()) return;

        if (packet.getRawPacket() instanceof WrappedPacketInUseEntity)
        {
            if (((WrappedPacketInUseEntity) packet.getRawPacket()).getAction()
                == WrappedPacketInUseEntity.EntityUseAction.ATTACK)
            {
                double pitch = Math.abs(
                    getStorage().getRotationProcessor().getPitch() - getStorage().getRotationProcessor()
                        .getLastPitch());

                if (pitch % 0.5 == 0.0 && pitch % 1.5f != 0.0)
                {
                    if (increaseBuffer() > 3)
                    {
                        fail("moved extremely rounded", String.format("pitch=%.3f", pitch), 1);
                    }
                }
                else
                {
                    decreaseBufferBy(0.125);
                }
            }
        }
    }
}
