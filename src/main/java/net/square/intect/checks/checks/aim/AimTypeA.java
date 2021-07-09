package net.square.intect.checks.checks.aim;

import io.github.retrooper.packetevents.packetwrappers.NMSPacket;
import io.github.retrooper.packetevents.packetwrappers.play.in.flying.WrappedPacketInFlying;
import net.minecraft.server.v1_8_R3.PacketPlayInFlying;
import net.square.intect.checks.objectable.Check;
import net.square.intect.checks.objectable.CheckInfo;
import net.square.intect.checks.objectable.IntectPacket;
import net.square.intect.processor.custom.RotationProcessor;
import net.square.intect.processor.data.PlayerStorage;

@CheckInfo(name = "Aim", type = "A", description = "Hit miss ratio", maxVL = 20)
public class AimTypeA extends Check
{
    public AimTypeA(PlayerStorage data)
    {
        super(data);
    }

    @Override
    public void handle(IntectPacket packet)
    {

        if (shouldBypass()) return;

        if (packet.getRawPacket() instanceof PacketPlayInFlying)
        {
            WrappedPacketInFlying wrapped = new WrappedPacketInFlying(new NMSPacket(packet.getRawPacket()));

            if (wrapped.isLook() && getStorage().getCombatProcessor().getHitTicks() < 5)
            {

                RotationProcessor rotationProcessor = getStorage().getRotationProcessor();

                float deltaYaw = rotationProcessor.getDeltaYaw();
                float deltaPitch = rotationProcessor.getDeltaPitch();

                final boolean invalid = deltaYaw > .5F && deltaPitch < .0001 && deltaPitch > 0;

                if (invalid)
                {
                    if (increaseBuffer() > 4)
                    {
                        fail("rotated invalid", String.format("dY %.3f > .5F, dP %.3f < .0001", deltaYaw, deltaPitch),
                             1);
                    }
                }
                else
                {
                    decreaseBufferBy(0.25);
                }
            }
        }
    }
}