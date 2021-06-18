package net.square.intect.checks.checks.killaura;

import net.minecraft.server.v1_8_R3.PacketPlayInFlying;
import net.square.intect.checks.objectable.Check;
import net.square.intect.checks.objectable.CheckInfo;
import net.square.intect.checks.objectable.IntectPacket;
import net.square.intect.processor.data.PlayerStorage;
import net.square.intect.utils.MathUtil;

@CheckInfo(name = "Killaura", type = "R", description = "Checks for gcd bypass", maxVL = 20)
public class KillauraTypeR extends Check {

    public KillauraTypeR(PlayerStorage data) {
        super(data);
    }

    private int threshold = 0;

    @Override
    public void handle(IntectPacket packet) {

        if (shouldBypass()) return;

        if(packet.getRawPacket() instanceof PacketPlayInFlying.PacketPlayInPositionLook) {

            final float deltaPitch = getStorage().getRotationProcessor().getDeltaPitch();
            final float lastDeltaPitch = getStorage().getRotationProcessor().getLastDeltaPitch();

            if (deltaPitch > 0.5) {

                final long expandedPitch = (long) (deltaPitch * MathUtil.EXPANDER);
                final long lastExpandedPitch = (long) (lastDeltaPitch * MathUtil.EXPANDER);

                final double divisorPitch = MathUtil.getGcd(expandedPitch, lastExpandedPitch);
                final double constantPitch = divisorPitch / MathUtil.EXPANDER;

                final double pitch = getStorage().getRotationProcessor().getPitch();
                final double moduloPitch = Math.abs(pitch % constantPitch);

                if (moduloPitch < 1.5E-5) {
                    if (threshold++ > 2) {
                        fail();
                    }
                } else {
                    threshold -= threshold > 0 ? 1 : 0;
                }
            }
        }
    }
}
