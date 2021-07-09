package net.square.intect.checks.checks.aim;

import io.github.retrooper.packetevents.packetwrappers.NMSPacket;
import io.github.retrooper.packetevents.packetwrappers.play.in.useentity.WrappedPacketInUseEntity;
import net.minecraft.server.v1_8_R3.PacketPlayInUseEntity;
import net.square.intect.checks.objectable.Check;
import net.square.intect.checks.objectable.CheckInfo;
import net.square.intect.checks.objectable.IntectPacket;
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

        if(shouldBypass()) return;

        if (packet.getRawPacket() instanceof PacketPlayInUseEntity)
        {
            WrappedPacketInUseEntity wrappedPacketInUseEntity = new WrappedPacketInUseEntity(
                new NMSPacket(packet.getRawPacket()));

            if (wrappedPacketInUseEntity.getAction() == WrappedPacketInUseEntity.EntityUseAction.ATTACK)
            {
                double pitch = Math.abs(
                    getStorage().getRotationProcessor().getPitch() - getStorage().getRotationProcessor()
                        .getLastPitch());

                if (pitch % 0.5 == 0.0 && pitch % 1.5f != 0.0)
                {
                    if(increaseBuffer() > 3) {
                        fail("moved extremly rounded", String.format("pitch=%.3f", pitch), 1);
                    }
                } else {
                    decreaseBufferBy(0.125);
                }
            }
        }
    }
}
