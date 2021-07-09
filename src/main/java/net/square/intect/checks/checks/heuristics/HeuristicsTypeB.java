package net.square.intect.checks.checks.heuristics;

import net.square.intect.checks.objectable.Check;
import net.square.intect.checks.objectable.CheckInfo;
import net.square.intect.checks.objectable.IntectPacket;
import net.square.intect.processor.data.PlayerStorage;
import net.square.intect.utils.MathUtil;
import org.bukkit.event.EventHandler;
import org.bukkit.event.player.PlayerMoveEvent;

@CheckInfo(name = "Heuristics", type = "B", description = "Checks for invalid victim", maxVL = 20, bukkit = true)
public class HeuristicsTypeB extends Check
{

    public HeuristicsTypeB(PlayerStorage data)
    {
        super(data);
    }

    @EventHandler
    public void handle(PlayerMoveEvent event)
    {

        if (shouldBypass()) return;

        final float deltaYaw = getStorage().getRotationProcessor().getDeltaYaw();
        final float deltaPitch = getStorage().getRotationProcessor().getDeltaPitch();

        final double expander = Math.pow(2.0, 24.0);

        if (deltaYaw == 0.0f || deltaPitch == 0.0f) return;

        final float gcd = (float) MathUtil.getVictim(
            (long) (deltaPitch * expander),
            (long) (getStorage().getRotationProcessor().getLastDeltaPitch() * expander));

        if (gcd < 131072.0f)
        {
            if (increaseBuffer() > 2)
            {
                fail("rotate invalid", String.format("gcd %.3f < 131072.0", gcd), 1);
            }
        }
        else
        {
            decreaseBufferBy(0.25);
        }
    }

    @Override
    public void handle(IntectPacket packet)
    {
    }
}
