package net.square.intect.checks.checks.killaura;

import com.google.common.collect.Lists;
import net.minecraft.server.v1_8_R3.PacketPlayInFlying;
import net.square.intect.checks.objectable.Check;
import net.square.intect.checks.objectable.CheckInfo;
import net.square.intect.checks.objectable.IntectPacket;
import net.square.intect.processor.data.PlayerStorage;

import java.util.Deque;
import java.util.concurrent.atomic.AtomicInteger;

@CheckInfo(name = "Killaura", type = "H", description = "Rotation samples", maxVL = 20)
public class KillauraTypeH extends Check {

    public KillauraTypeH(PlayerStorage data) {
        super(data);
    }

    private final Deque<Float> samplesYaw = Lists.newLinkedList();
    private final Deque<Float> samplesPitch = Lists.newLinkedList();

    @Override
    public void handle(IntectPacket packet) {

        if (shouldBypass()) return;

        if(packet.getRawPacket() instanceof PacketPlayInFlying.PacketPlayInPositionLook) {

            if (getStorage().getRotationProcessor().getLastDeltaYaw() > 0.0
                && getStorage().getRotationProcessor().getLastDeltaPitch() > 0.0) {

                samplesPitch.add(getStorage().getRotationProcessor().getLastDeltaPitch());
                samplesYaw.add(getStorage().getRotationProcessor().getLastDeltaYaw());
            }

            if (samplesPitch.size() == 10 && samplesYaw.size() == 10) {
                final AtomicInteger level = new AtomicInteger(0);

                double sum = 0;
                long count = 0;
                for (Float d1 : samplesYaw) {
                    double v = d1;
                    sum += v;
                    count++;
                }
                final double averageYaw = count > 0 ? sum / count : 0.0;
                double result = 0;
                long count1 = 0;
                for (Float d : samplesPitch) {
                    double v = d;
                    result += v;
                    count1++;
                }
                final double averagePitch = count1 > 0 ? result / count1 : 0.0;

                for (Float aFloat : samplesYaw) {
                    if (aFloat % 1.0 == 0.0) {
                        level.incrementAndGet();
                    }
                }
                for (Float delta : samplesPitch) {
                    if (delta % 1.0 == 0.0) {
                        level.incrementAndGet();
                    }
                }

                if (level.get() >= 8 && averageYaw > 1.d && averagePitch > 1.d) {
                    fail();
                }
                samplesYaw.clear();
                samplesPitch.clear();
            }
        }
    }
}
