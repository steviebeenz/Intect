package net.square.intect.checks.impl.heuristics;

import net.square.intect.checks.objectable.Check;
import net.square.intect.checks.objectable.CheckInfo;
import net.square.intect.utils.objectable.IntectPacket;
import net.square.intect.processor.data.PlayerStorage;
import org.bukkit.event.EventHandler;
import org.bukkit.event.player.PlayerMoveEvent;

@CheckInfo(name = "Heuristics", type = "A", description = "Invalid delta combat", maxVL = 20, bukkit = true)
public class HeuristicsTypeA extends Check
{

    public HeuristicsTypeA(PlayerStorage data)
    {
        super(data);
    }

    @EventHandler
    public void handle(PlayerMoveEvent event)
    {

        if (shouldBypass()) return;

        final float deltaYaw = getStorage().getRotationProcessor().getDeltaYaw();
        final float deltaPitch = getStorage().getRotationProcessor().getDeltaPitch();

        if (deltaPitch < 0.1 && deltaYaw > 3.5)
        {
            if (increaseBuffer() > 8)
            {

                fail("rotated invalid", "dP < 0.1, dY > 3.5", 1);
            }
        }
        else
        {
            decreaseBufferBy(2.25);
        }
    }

    @Override
    public void handle(IntectPacket packet)
    {
    }
}
