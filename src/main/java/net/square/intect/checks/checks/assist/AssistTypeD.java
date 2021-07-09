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

@CheckInfo(name = "Assist", type = "D", description = "Checks for invalid deviation", maxVL = 20)
public class AssistTypeD extends Check
{

    public AssistTypeD(PlayerStorage data)
    {
        super(data);
    }

    private final EvictingList<Double> assistDifferenceSamples = new EvictingList<>(25);

    @Override
    public void handle(IntectPacket packet)
    {

        if (shouldBypass()) return;

        if (packet.getRawPacket() instanceof PacketPlayInFlying.PacketPlayInPositionLook)
        {
            final Entity target = getStorage().getCombatProcessor().getTarget();

            if (target != null)
            {
                final Location origin = packet.getPlayer().getLocation().clone();
                final Vector end = target.getLocation().clone().toVector();

                final float optimalYaw = origin.setDirection(end.subtract(origin.toVector())).getYaw() % 360F;
                final float rotationYaw = getStorage().getRotationProcessor().getYaw();
                final float deltaYaw = getStorage().getRotationProcessor().getDeltaYaw();
                final float fixedRotYaw = (rotationYaw % 360F + 360F) % 360F;

                final double difference = Math.abs(fixedRotYaw - optimalYaw);

                if (deltaYaw > 3f)
                {
                    assistDifferenceSamples.add(difference);
                }
                if (assistDifferenceSamples.isFull())
                {
                    final double average = MathUtil.getAverage(assistDifferenceSamples);
                    final double deviation = MathUtil.getStandardDeviation(assistDifferenceSamples);

                    final boolean invalid = average < 7 && deviation < 12;

                    if (invalid)
                    {
                        if (increaseBuffer() > 3)
                        {
                            fail("moved invalid", "avg < 7, dev < 12", 1);
                        }
                    }
                    else
                    {
                        decreaseBufferBy(0.25);
                    }
                    assistDifferenceSamples.remove(0);
                }
            }
        }
    }
}
