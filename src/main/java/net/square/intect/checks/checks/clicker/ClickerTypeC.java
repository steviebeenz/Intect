package net.square.intect.checks.checks.clicker;

import net.minecraft.server.v1_8_R3.PacketPlayInArmAnimation;
import net.minecraft.server.v1_8_R3.PacketPlayInFlying;
import net.square.intect.checks.objectable.Check;
import net.square.intect.checks.objectable.CheckInfo;
import net.square.intect.checks.objectable.IntectPacket;
import net.square.intect.processor.data.PlayerStorage;
import net.square.intect.utils.MathUtil;

import java.util.ArrayDeque;

@CheckInfo(name = "Clicker", type = "C", description = "Checks for rounded cps", maxVL = 20, experimental = true)
public class ClickerTypeC
    extends Check
{
    public ClickerTypeC(PlayerStorage data)
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

            if (samples.size() == 50)
            {

                final double cps = MathUtil.getCps(samples);
                final double difference = Math.abs(Math.round(cps) - cps);

                if (difference < 0.001)
                {
                    if (increaseBuffer() > 5)
                    {
                        fail("is clicking invalid", String.format("diff=%.3f", difference), 1);
                    }
                }
                else
                {
                    decreaseBufferBy(1);
                }

                samples.clear();
            }
        }
        else if (packet.getRawPacket() instanceof PacketPlayInFlying)
        {
            ++ticks;
        }
    }
}
