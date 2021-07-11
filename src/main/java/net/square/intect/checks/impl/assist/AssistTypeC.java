package net.square.intect.checks.impl.assist;

import io.github.retrooper.packetevents.packetwrappers.play.in.flying.WrappedPacketInFlying;
import net.square.intect.checks.objectable.Check;
import net.square.intect.checks.objectable.CheckInfo;
import net.square.intect.utils.objectable.IntectPacket;
import net.square.intect.processor.data.PlayerStorage;

@CheckInfo(name = "Assist", type = "C", description = "Snappy rotations check", maxVL = 5, experimental = true)
public class AssistTypeC extends Check
{
    public AssistTypeC(PlayerStorage data)
    {
        super(data);
    }

    private float lastDeltaYaw = 0f;
    private float lastLastDeltaYaw = 0f;

    @Override
    public void handle(IntectPacket packet)
    {

        if (shouldBypass()) return;

        if (packet.getRawPacket() instanceof WrappedPacketInFlying)
        {
            if (((WrappedPacketInFlying) packet.getRawPacket()).isLook())
            {
                final float deltaYaw = this.getStorage().getRotationProcessor().getDeltaYaw();

                if (deltaYaw < 5.0f && lastDeltaYaw > 20.0f && lastLastDeltaYaw < 5.0f)
                {
                    fail("moved invalid", "dY < 5.0 lDY > 20.0", 1);
                }

                this.debug(
                    String.format("low=%.2f, high=%.2f", (deltaYaw + this.lastLastDeltaYaw) / 2.0f, this.lastDeltaYaw));

                this.lastLastDeltaYaw = this.lastDeltaYaw;
                this.lastDeltaYaw = deltaYaw;
            }
        }
    }
}
