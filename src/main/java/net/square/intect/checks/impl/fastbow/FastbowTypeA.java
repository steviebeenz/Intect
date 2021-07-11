package net.square.intect.checks.impl.fastbow;

import net.square.intect.checks.objectable.Check;
import net.square.intect.checks.objectable.CheckInfo;
import net.square.intect.utils.objectable.IntectPacket;
import net.square.intect.processor.data.PlayerStorage;
import org.bukkit.Material;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.block.Action;
import org.bukkit.event.entity.EntityShootBowEvent;
import org.bukkit.event.player.PlayerInteractEvent;

@CheckInfo(name = "Fastbow", type = "A", description = "Checks for packet delay", maxVL = 20, bukkit = true)
public class FastbowTypeA extends Check
{

    public FastbowTypeA(PlayerStorage data)
    {
        super(data);
    }

    long lastPowPul = 0;

    @EventHandler
    public void handle(EntityShootBowEvent event)
    {

        if (!isEnabled()) return;

        if (!(event.getEntity() instanceof Player)) return;

        double force = event.getForce();
        long lastBowPul = System.currentTimeMillis() - lastPowPul;
        double pullBackSpeed = force / (double) lastBowPul;

        if ((pullBackSpeed >= 0.01 || pullBackSpeed == Double.POSITIVE_INFINITY))
        {
            fail("pulled bow invalid", "pBS >= 0.01 | PI", 2);
            event.setCancelled(true);
        }
    }

    @EventHandler
    public void handle(PlayerInteractEvent event)
    {

        if (!isEnabled()) return;

        Player player = event.getPlayer();
        Action action = event.getAction();

        if ((action.equals(Action.RIGHT_CLICK_BLOCK) || action.equals(Action.RIGHT_CLICK_AIR)) &&
            player.getItemInHand() != null && player.getItemInHand().getType().equals(Material.BOW))
        {
            lastPowPul = System.currentTimeMillis();
        }
    }

    @Override
    public void handle(IntectPacket packet)
    {
    }
}
