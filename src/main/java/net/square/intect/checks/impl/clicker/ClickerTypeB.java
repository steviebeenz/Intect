package net.square.intect.checks.impl.clicker;

import io.github.retrooper.packetevents.packetwrappers.play.in.flying.WrappedPacketInFlying;
import net.square.intect.checks.objectable.Check;
import net.square.intect.checks.objectable.CheckInfo;
import net.square.intect.utils.objectable.IntectPacket;
import net.square.intect.processor.custom.custom.WrappedPacketInArmAnimation;
import net.square.intect.processor.data.PlayerStorage;
import net.square.intect.utils.MathUtil;

import java.util.ArrayDeque;

@CheckInfo(name = "Clicker", type = "B", description = "Checks for statistics", maxVL = 20, experimental = true)
public class ClickerTypeB
    extends Check
{
    public ClickerTypeB(PlayerStorage data)
    {
        super(data);
    }

    private int ticks = 0;
    private double lastDev = 0.0, lastSkew = 0.0, lastKurt = 0.0;
    private final ArrayDeque<Integer> samples = new ArrayDeque<>();

    @Override
    public void handle(IntectPacket packet)
    {

        if (shouldBypass()) return;

        if (packet.getRawPacket() instanceof WrappedPacketInArmAnimation)
        {
            if (ticks < 4)
            {
                samples.add(ticks);
            }

            if (samples.size() == 120)
            {
                final double deviation = MathUtil.getStandardDeviation(samples);
                final double skewness = MathUtil.getSkewness(samples);
                final double kurtosis = MathUtil.getKurtosis(samples);

                final double deltaDeviation = Math.abs(deviation - lastDev);
                final double deltaSkewness = Math.abs(skewness - lastSkew);
                final double deltaKurtosis = Math.abs(kurtosis - lastKurt);

                if (deltaDeviation < 0.01 || deltaSkewness < 0.01 || deltaKurtosis < 0.01)
                {
                    if (increaseBuffer() > 5)
                    {
                        fail("is clicking invalid",
                             String.format("dd=%.4f ds=%.4f dk=%.4f", deltaDeviation, deltaSkewness, deltaKurtosis), 1);
                    }
                }
                else
                {
                    decreaseBufferBy(1);
                }

                lastDev = deviation;
                lastSkew = skewness;
                lastKurt = kurtosis;

                samples.clear();
            }
        }
        else if (packet.getRawPacket() instanceof WrappedPacketInFlying)
        {
            ++ticks;
        }
    }
}
