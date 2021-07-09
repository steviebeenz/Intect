package net.square.intect.checks.checks.clicker;

import net.minecraft.server.v1_8_R3.PacketPlayInArmAnimation;
import net.minecraft.server.v1_8_R3.PacketPlayInFlying;
import net.square.intect.checks.objectable.Check;
import net.square.intect.checks.objectable.CheckInfo;
import net.square.intect.checks.objectable.IntectPacket;
import net.square.intect.processor.data.PlayerStorage;
import net.square.intect.utils.MathUtil;
import net.square.intect.utils.objectable.Pair;

import java.util.ArrayDeque;
import java.util.List;

@CheckInfo(name = "Clicker", type = "D", description = "Checks for consistency", maxVL = 20, experimental = true)
public class ClickerTypeD
    extends Check
{
    public ClickerTypeD(PlayerStorage data)
    {
        super(data);
    }

    private final ArrayDeque<Integer> samples = new ArrayDeque<>();
    private int ticks = 0;

    @Override
    public void handle(IntectPacket packet)
    {

        if (shouldBypass()) return;

        if (packet.getRawPacket() instanceof PacketPlayInArmAnimation)
        {
            if (ticks < 4)
            {
                samples.add(ticks);
            }

            if(samples.size() == 20) {
                Pair<List<Double>, List<Double>> outlierPair = MathUtil.getOutliers(samples);

                int outliers = outlierPair.getX().size() + outlierPair.getY().size();
                int duplicates = (int) (samples.size() - samples.stream().distinct().count());

                if (outliers < 2 && duplicates > 16)
                {
                    if (increaseBuffer() > 5)
                    {
                        fail("is clicking invalid", String.format("outliers=%d dupl=%d", outliers, duplicates), 1);
                    }
                }
                else
                {
                    decreaseBufferBy(1);
                }
                samples.clear();
            }
            ticks = 0;
        }
        else if (packet.getRawPacket() instanceof PacketPlayInFlying)
        {
            ++ticks;
        }
    }
}
