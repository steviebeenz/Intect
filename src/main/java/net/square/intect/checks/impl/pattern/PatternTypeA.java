package net.square.intect.checks.impl.pattern;

import io.github.retrooper.packetevents.packetwrappers.play.in.flying.WrappedPacketInFlying;
import net.square.intect.checks.objectable.Check;
import net.square.intect.checks.objectable.CheckInfo;
import net.square.intect.utils.objectable.IntectPacket;
import net.square.intect.processor.custom.RotationProcessor;
import net.square.intect.processor.data.PlayerStorage;

@CheckInfo(name = "Pattern", type = "A", maxVL = 5, description = "Checks for liquid heuristics", experimental = true)
public class PatternTypeA extends Check
{

    public PatternTypeA(PlayerStorage data)
    {
        super(data);
    }

    private double lastLiquidYaw = 0.0;
    private double lastLiquidPitch = 0.0;

    @Override
    public void handle(IntectPacket packet)
    {

        if (packet.getRawPacket() instanceof WrappedPacketInFlying)
        {
            if (((WrappedPacketInFlying) packet.getRawPacket()).isLook())
            {
                if (shouldBypass()) return;

                RotationProcessor processor = getStorage().getRotationProcessor();

                double f = processor.getFinalSensitivity() * 0.6F + 0.2F;
                double gcd = f * f * f * 1.2F;

                double deltaYaw = processor.getDeltaYaw() % gcd;
                double deltaPitch = processor.getDeltaPitch() % gcd;

                double yaw = deltaYaw - this.lastLiquidYaw;
                double pitch = deltaPitch - this.lastLiquidPitch;

                double sync = Math.abs(deltaYaw + deltaPitch + processor.getGcd());

                //if (pitch != 0.0 && yaw != 0.0)
                //{
                //    debug(String.format("pitch=%.2f yaw=%.2f sync:%.2f threshold:%d >= 2", Math.abs(pitch), Math
                //    .abs(yaw), sync,
                //                        threshold));
                //}

                this.lastLiquidYaw = deltaYaw;
                this.lastLiquidPitch = deltaPitch;
            }
        }
    }
}
