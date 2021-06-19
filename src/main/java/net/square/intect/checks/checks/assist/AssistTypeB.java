package net.square.intect.checks.checks.assist;

import net.minecraft.server.v1_8_R3.PacketPlayInFlying;
import net.square.intect.checks.objectable.Check;
import net.square.intect.checks.objectable.CheckInfo;
import net.square.intect.checks.objectable.IntectPacket;
import net.square.intect.processor.data.PlayerStorage;
import net.square.intect.utils.MathUtil;
import net.square.intect.utils.objectable.EvictingList;

@CheckInfo(name = "Assist", type = "B", description = "Checks for extreme smooth rotations", maxVL = 20)
public class AssistTypeB extends Check {

    public AssistTypeB(PlayerStorage data) {
        super(data);
    }

    private final EvictingList<Float> yawAccelSamples = new EvictingList<>(20);
    private final EvictingList<Float> pitchAccelSamples = new EvictingList<>(20);

    private int threshold = 0;

    @Override
    public void handle(IntectPacket packet) {

        if (shouldBypass()) return;

        if(packet.getRawPacket() instanceof PacketPlayInFlying) {
            float yawAccel = getStorage().getRotationProcessor().getJoltYaw();
            float pitchAccel = getStorage().getRotationProcessor().getJoltPitch();

            final float deltaYaw = getStorage().getRotationProcessor().getDeltaYaw();

            yawAccelSamples.add(yawAccel);
            pitchAccelSamples.add(pitchAccel);

            if (yawAccelSamples.isFull() && pitchAccelSamples.isFull()) {
                final double yawAccelAverage = MathUtil.getAverage(yawAccelSamples);
                final double pitchAccelAverage = MathUtil.getAverage(pitchAccelSamples);

                final double yawAccelDeviation = MathUtil.getVariance(yawAccelSamples);
                final double pitchAccelDeviation = MathUtil.getVariance(pitchAccelSamples);

                final boolean exemptRotation = deltaYaw < 1.5F;
                final boolean averageInvalid = yawAccelAverage < 1 || pitchAccelAverage < 1 && !exemptRotation;
                final boolean deviationInvalid = yawAccelDeviation < (5 * 5) && pitchAccelDeviation > (5 * 5)
                    && !exemptRotation;

                if (averageInvalid && deviationInvalid) {
                    if (threshold++ > 8) {
                        fail();
                    }
                } else {
                    threshold -= threshold > 0 ? 1 : 0;
                }
                yawAccelSamples.remove(0);
                pitchAccelSamples.remove(0);
            }
        }
    }
}