package net.square.intect.checks.impl.pattern;

import io.github.retrooper.packetevents.packetwrappers.play.in.flying.WrappedPacketInFlying;
import net.square.intect.checks.objectable.Check;
import net.square.intect.checks.objectable.CheckInfo;
import net.square.intect.processor.data.PlayerStorage;
import net.square.intect.utils.MathUtil;
import net.square.intect.utils.objectable.EvictingList;
import net.square.intect.utils.objectable.IntectPacket;

@CheckInfo(name = "Pattern", type = "B", description = "Checks for sens gcd", maxVL = 1)
public class PatternTypeA extends Check
{
    public PatternTypeA(PlayerStorage data)
    {
        super(data);
    }

    private final EvictingList<Double> samples = new EvictingList<>(100);

    private double lastSens = 0;

    @Override
    public void handle(IntectPacket packet)
    {
        if (packet.getRawPacket() instanceof WrappedPacketInFlying)
        {
            if (((WrappedPacketInFlying) packet.getRawPacket()).isLook())
            {

                double finalSensitivity = getStorage().getRotationProcessor().getFinalSensitivity();

                samples.add(MathUtil.getGcd(Math.abs(finalSensitivity), Math.abs(lastSens)));

                if (samples.isFull())
                {

                    double average = MathUtil.getAverage(samples);

                    if (average > 200.00)
                    {
                        if (increaseBuffer() > 1)
                        {
                            fail("rotated invalid", String.format("avg=%.2f", average), 1);
                        }
                    }
                    else
                    {
                        decreaseBufferBy(0.5);
                    }

                    debug(String.format("average=%.2f", average));
                    samples.clear();
                }
                lastSens = finalSensitivity;
            }
        }
    }
}
