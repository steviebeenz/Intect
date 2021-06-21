package net.square.intect.checks.checks.assist;

import net.minecraft.server.v1_8_R3.PacketPlayInFlying;
import net.square.intect.checks.objectable.Check;
import net.square.intect.checks.objectable.CheckInfo;
import net.square.intect.checks.objectable.IntectPacket;
import net.square.intect.processor.data.PlayerStorage;
import net.square.intect.utils.MathUtil;
import net.square.intect.utils.objectable.EvictingList;
import org.bukkit.Location;
import org.bukkit.entity.Entity;
import org.bukkit.util.Vector;

@CheckInfo(name = "Assist", type = "C", description = "Checks for generic flaw", maxVL = 20)
public class AssistTypeC extends Check
{

    public AssistTypeC(PlayerStorage data)
    {
        super(data);
    }

    private final EvictingList<Double> differenceSamples = new EvictingList<>(25);

    private int threshold = 0;

    @Override
    public void handle(IntectPacket packet)
    {

        if (shouldBypass()) return;

        if (packet.getRawPacket() instanceof PacketPlayInFlying.PacketPlayInPositionLook)
        {

            Entity target = getStorage().getCombatProcessor().getTarget();

            if (target == null) return;

            Location origin = packet.getPlayer().getLocation().clone();
            Vector end = target.getLocation().clone().toVector();
            float optimalYaw = origin.setDirection(end.subtract(origin.toVector())).getYaw() % 360.0f;
            float fixedRotYaw = (getStorage().getRotationProcessor().getYaw() % 360.0f + 360.0f) % 360.0f;
            double difference = Math.abs(fixedRotYaw - optimalYaw);
            if ((getStorage().getRotationProcessor().getYaw() - getStorage().getRotationProcessor().getLastYaw())
                > 3.0f)
            {
                differenceSamples.add(difference);
            }
            if (differenceSamples.isFull())
            {
                final double average = MathUtil.getAverage(differenceSamples);
                final double deviation = MathUtil.getStandardDeviation(differenceSamples);
                final boolean invalid = average < 7.0 && deviation < 12.0;

                if (invalid)
                {
                    if (threshold++ > 3)
                    {
                        fail();
                    }
                }
                else
                {
                    threshold -= threshold > 0 ? 1 : 0;
                }
                differenceSamples.remove(0);
            }
        }
    }
}
