package net.square.intect.checks.checks.assist;

import net.minecraft.server.v1_8_R3.PacketPlayInFlying;
import net.square.intect.checks.objectable.Check;
import net.square.intect.checks.objectable.CheckInfo;
import net.square.intect.checks.objectable.IntectPacket;
import net.square.intect.processor.data.PlayerStorage;

@CheckInfo(name = "Assist", type = "E", description = "Snappy rotations check", maxVL = 5, experimental = true)
public class AssistTypeE extends Check
{
    public AssistTypeE(PlayerStorage data)
    {
        super(data);
    }

    private float lastDeltaYaw = 0f;
    private float lastLastDeltaYaw = 0f;

    @Override
    public void handle(IntectPacket packet)
    {

        if (shouldBypass()) return;

        if (packet.getRawPacket() instanceof PacketPlayInFlying.PacketPlayInPositionLook)
        {

            final float deltaYaw = this.getStorage().getRotationProcessor().getDeltaYaw();

            if (deltaYaw < 5.0f && lastDeltaYaw > 20.0f && lastLastDeltaYaw < 5.0f)
            {
                fail();
            }

            this.debug(
                String.format("low=%.2f, high=%.2f", (deltaYaw + this.lastLastDeltaYaw) / 2.0f, this.lastDeltaYaw));

            this.lastLastDeltaYaw = this.lastDeltaYaw;
            this.lastDeltaYaw = deltaYaw;
        }
    }
}
