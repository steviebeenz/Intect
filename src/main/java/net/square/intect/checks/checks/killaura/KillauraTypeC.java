package net.square.intect.checks.checks.killaura;

import net.square.intect.checks.objectable.Check;
import net.square.intect.checks.objectable.CheckInfo;
import net.square.intect.checks.objectable.IntectPacket;
import net.square.intect.processor.data.PlayerStorage;
import org.bukkit.event.EventHandler;
import org.bukkit.event.player.PlayerMoveEvent;

@CheckInfo(name = "Killaura", type = "C", description = "Checks for sprint acceleration", maxVL = 20, bukkit = true)
public class KillauraTypeC extends Check
{

    public KillauraTypeC(PlayerStorage data)
    {
        super(data);
    }

    private double lastMovement = 0.0;
    private double lastAccel = 0.0;

    private int threshold = 0;

    @EventHandler
    public void handle(PlayerMoveEvent event)
    {

        if (shouldBypass()) return;

        final double movement = Math.hypot(
            event.getTo().getX() - event.getFrom().getX(), event.getTo().getZ() - event.getFrom().getZ());

        final double accel = Math.abs(movement - lastMovement);

        if ((event.getPlayer().isSprinting() || movement > 0.27) && accel < 0.01 && lastAccel < 0.01)
        {

            if (++threshold > 10)
            {
                fail();
            }
        }
        else if (threshold > 0)
        {
            threshold -= 3;
        }
        lastAccel = accel;
        lastMovement = movement;
    }

    @Override
    public void handle(IntectPacket packet)
    {
    }
}
