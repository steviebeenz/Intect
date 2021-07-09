package net.square.intect.checks.checks.aim;

import io.github.retrooper.packetevents.packetwrappers.NMSPacket;
import io.github.retrooper.packetevents.packetwrappers.play.in.useentity.WrappedPacketInUseEntity;
import net.minecraft.server.v1_8_R3.PacketPlayInUseEntity;
import net.square.intect.checks.objectable.Check;
import net.square.intect.checks.objectable.CheckInfo;
import net.square.intect.checks.objectable.IntectPacket;
import net.square.intect.processor.data.PlayerStorage;
import net.square.intect.utils.MathUtil;

@CheckInfo(name = "Aim", type = "E", description = "Checks for gcd path", maxVL = 20)
public class AimTypeE
    extends Check
{
    public AimTypeE(PlayerStorage data)
    {
        super(data);
    }

    private double lastPitchDifference = 0;
    private double lastYawDifference = 0;

    @Override
    public void handle(IntectPacket packet)
    {
        if (shouldBypass()) return;

        if (packet.getRawPacket() instanceof PacketPlayInUseEntity)
        {
            WrappedPacketInUseEntity wrappedPacketInUseEntity = new WrappedPacketInUseEntity(
                new NMSPacket(packet.getRawPacket()));

            if (wrappedPacketInUseEntity.getAction() == WrappedPacketInUseEntity.EntityUseAction.ATTACK)
            {
                double pitchDifference = Math.abs(
                    getStorage().getRotationProcessor().getPitch() - getStorage().getRotationProcessor()
                        .getLastPitch());

                double yawDifference = Math.abs(
                    getStorage().getRotationProcessor().getYaw() - getStorage().getRotationProcessor().getLastYaw());

                double yawAccel = Math.abs(pitchDifference - lastPitchDifference);
                double pitchAccel = Math.abs(yawDifference - lastYawDifference);

                if (yawDifference > 3.0F && pitchDifference <= 10.0F && yawAccel > 2F && pitchAccel > 2F
                    && pitchDifference < yawDifference)
                {

                    double pitchGCD = MathUtil.getGcd(pitchDifference, lastPitchDifference);

                    if (pitchGCD < 0.009)
                    {
                        if (increaseBuffer() > 3)
                        {
                            fail("rotated invalid", String.format("gcd=%.4f", pitchGCD), 1);
                        }
                    }
                    else
                    {
                        decreaseBufferBy(0.25);
                    }
                }

                lastYawDifference = yawDifference;
                lastPitchDifference = pitchDifference;
            }
        }
    }
}
