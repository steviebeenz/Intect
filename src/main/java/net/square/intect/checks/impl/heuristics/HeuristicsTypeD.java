package net.square.intect.checks.impl.heuristics;

import com.google.common.collect.Lists;
import io.github.retrooper.packetevents.packetwrappers.play.in.flying.WrappedPacketInFlying;
import net.square.intect.checks.objectable.Check;
import net.square.intect.checks.objectable.CheckInfo;
import net.square.intect.utils.objectable.IntectPacket;
import net.square.intect.processor.data.PlayerStorage;

import java.util.Deque;
import java.util.concurrent.atomic.AtomicInteger;

@CheckInfo(name = "Heuristics", type = "D", description = "Rotation samples", maxVL = 20)
public class HeuristicsTypeD extends Check
{

    public HeuristicsTypeD(PlayerStorage data)
    {
        super(data);
    }

    private final Deque<Float> samplesYaw = Lists.newLinkedList();
    private final Deque<Float> samplesPitch = Lists.newLinkedList();

    @Override
    public void handle(IntectPacket packet)
    {

        if (shouldBypass()) return;

        if (packet.getRawPacket() instanceof WrappedPacketInFlying)
        {

            if (((WrappedPacketInFlying) packet.getRawPacket()).isLook())
            {
                if (getStorage().getRotationProcessor().getLastDeltaYaw() > 0.0
                    && getStorage().getRotationProcessor().getLastDeltaPitch() > 0.0)
                {

                    samplesPitch.add(getStorage().getRotationProcessor().getLastDeltaPitch());
                    samplesYaw.add(getStorage().getRotationProcessor().getLastDeltaYaw());
                }

                if (samplesPitch.size() == 10 && samplesYaw.size() == 10)
                {
                    final AtomicInteger level = new AtomicInteger(0);

                    double sum = 0;
                    long count = 0;
                    for (Float d1 : samplesYaw)
                    {
                        double v = d1;
                        sum += v;
                        count++;
                    }
                    final double averageYaw = count > 0 ? sum / count : 0.0;
                    double result = 0;
                    long count1 = 0;
                    for (Float d : samplesPitch)
                    {
                        double v = d;
                        result += v;
                        count1++;
                    }
                    final double averagePitch = count1 > 0 ? result / count1 : 0.0;

                    for (Float aFloat : samplesYaw)
                    {
                        if (aFloat % 1.0 == 0.0)
                        {
                            level.incrementAndGet();
                        }
                    }
                    for (Float delta : samplesPitch)
                    {
                        if (delta % 1.0 == 0.0)
                        {
                            level.incrementAndGet();
                        }
                    }

                    if (level.get() >= 8 && averageYaw > 1.d && averagePitch > 1.d)
                    {
                        fail("rotated invalid", "lvl >= 8, aY > 1.d, aP > 1.d", 1);
                    }
                    samplesYaw.clear();
                    samplesPitch.clear();
                }
            }
        }
    }
}
