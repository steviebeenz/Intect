package net.square.intect.checks.checks.assist;

import net.minecraft.server.v1_8_R3.PacketPlayInFlying;
import net.square.intect.checks.objectable.Check;
import net.square.intect.checks.objectable.CheckInfo;
import net.square.intect.checks.objectable.IntectPacket;
import net.square.intect.processor.data.PlayerStorage;
import net.square.intect.utils.MathUtil;

@CheckInfo(name = "Assist", type = "A", description = "Checks for modulo analysis", maxVL = 20)
public class AssistTypeA extends Check {

    public AssistTypeA(PlayerStorage data) {
        super(data);
    }

    private int threshold = 0;

    @Override
    public void handle(IntectPacket packet) {

        if (shouldBypass()) return;

        if (packet.getRawPacket() instanceof PacketPlayInFlying) {
            final float deltaYaw = getStorage().getRotationProcessor().getDeltaYaw();
            float deltaPitch = getStorage().getRotationProcessor().getDeltaPitch();

            final double divisorYaw = MathUtil.getGcd(
                (long) (deltaYaw * MathUtil.EXPANDER),
                (long) (getStorage().getRotationProcessor().getLastDeltaYaw() * MathUtil.EXPANDER)
            );
            final double divisorPitch = MathUtil.getGcd(
                (long) (deltaPitch * MathUtil.EXPANDER),
                (long) (getStorage().getRotationProcessor().getLastDeltaPitch() * MathUtil.EXPANDER)
            );

            final double constantYaw = divisorYaw / MathUtil.EXPANDER;
            final double constantPitch = divisorPitch / MathUtil.EXPANDER;

            final double currentX = deltaYaw / constantYaw;
            final double currentY = deltaPitch / constantPitch;

            final double previousX = getStorage().getRotationProcessor().getLastDeltaYaw() / constantYaw;
            final double previousY = getStorage().getRotationProcessor().getLastDeltaPitch() / constantPitch;

            if (deltaYaw > 0.0 && deltaPitch
                > 0.0 && deltaYaw < 20.f && deltaPitch < 20.f) {
                final double moduloX = currentX % previousX;
                final double moduloY = currentY % previousY;

                final double floorModuloX = Math.abs(Math.floor(moduloX) - moduloX);
                final double floorModuloY = Math.abs(Math.floor(moduloY) - moduloY);

                final boolean invalidX = moduloX > 90.d && floorModuloX > 0.1;
                final boolean invalidY = moduloY > 90.d && floorModuloY > 0.1;

                if (invalidX && invalidY) {
                    if (threshold++ > 3) {
                        fail();
                    }
                } else {
                    threshold -= threshold > 0 ? 1 : 0;
                }
            }
        }
    }
}
