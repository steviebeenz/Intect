package net.square.intect.checks.checks.killaura;

import net.minecraft.server.v1_8_R3.PacketPlayInFlying;
import net.square.intect.checks.objectable.Check;
import net.square.intect.checks.objectable.CheckInfo;
import net.square.intect.checks.objectable.IntectPacket;
import net.square.intect.processor.data.PlayerStorage;

@CheckInfo(name = "Heuristics", type = "G", description = "Checks for invalid rotations", maxVL = 20)
public class HeuristicsTypeG extends Check {

    public HeuristicsTypeG(PlayerStorage data) {
        super(data);
    }

    private double space = 0;

    private int threshold = 0;

    @Override
    public void handle(IntectPacket packet) {

        if (shouldBypass()) return;

        if (packet.getRawPacket() instanceof PacketPlayInFlying.PacketPlayInPositionLook) {

            final float deltaYaw = getStorage().getRotationProcessor().getDeltaYaw();
            final float lastDeltaYaw = getStorage().getRotationProcessor().getLastDeltaYaw();
            final float lastLastDeltaYaw = (float) space;

            if (deltaYaw < 5F && lastDeltaYaw > 20F && lastLastDeltaYaw < 5F) {
                if (threshold++ > 3) {
                    fail();
                }
            } else {
                threshold -= threshold > 0 ? 1 : 0;
            }
            space = lastDeltaYaw;
        }
    }
}
