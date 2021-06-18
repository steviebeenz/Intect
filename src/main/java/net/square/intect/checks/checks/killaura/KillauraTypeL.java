package net.square.intect.checks.checks.killaura;

import net.minecraft.server.v1_8_R3.PacketPlayInFlying;
import net.square.intect.checks.objectable.Check;
import net.square.intect.checks.objectable.CheckInfo;
import net.square.intect.checks.objectable.IntectPacket;
import net.square.intect.processor.data.PlayerStorage;

@CheckInfo(name = "Killaura", type = "L", description = "Checks for provocative movement", maxVL = 20)
public class KillauraTypeL extends Check {

    public KillauraTypeL(PlayerStorage data) {
        super(data);
    }

    private int threshold = 0;

    @Override
    public void handle(IntectPacket packet) {

        if (shouldBypass()) return;

        if (packet.getRawPacket() instanceof PacketPlayInFlying) {
            final float pitch = getStorage().getRotationProcessor().getPitch();
            final float deltaYaw =
                getStorage().getRotationProcessor().getYaw() - getStorage().getRotationProcessor().getLastYaw();
            final float deltaPitch = getStorage().getRotationProcessor().getDeltaPitch();

            final boolean invalidPitch = deltaPitch < 0.009 && validRotation(deltaYaw);
            final boolean invalidYaw = deltaYaw < 0.009 && validRotation(deltaPitch);

            final boolean invalid = (invalidPitch || invalidYaw) && pitch < 89f;

            if (invalid) {
                if (threshold++ > 20) {
                    fail();
                }
            } else {
                threshold -= threshold > 0 ? 1 : 0;
            }
        }
    }
    private boolean validRotation(float rotation) {
        return rotation > 2F && rotation < 35F;
    }
}
