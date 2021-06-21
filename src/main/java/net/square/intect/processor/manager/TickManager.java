package net.square.intect.processor.manager;

import lombok.Getter;
import net.square.intect.Intect;
import net.square.intect.processor.data.PlayerStorage;
import net.square.intect.utils.objectable.Pair;
import org.bukkit.Bukkit;
import org.bukkit.Location;
import org.bukkit.entity.Entity;
import org.bukkit.scheduler.BukkitTask;

import java.util.logging.Level;

public class TickManager implements Runnable
{

    @Getter
    private int ticks;
    private static BukkitTask task;

    public void start()
    {
        assert task == null : "TickProcessor has already been started!";

        task = Bukkit.getScheduler().runTaskTimer(Intect.getIntect(), this, 0L, 1L);
    }

    public void stop()
    {
        if (task == null) return;

        task.cancel();
        task = null;
    }

    @Override
    public void run()
    {
        ticks++;

        PlayerStorage.storageHashMap.forEach((player, data) ->
                                             {
                                                 final Entity target = data.getCombatProcessor().getTarget();
                                                 final Entity lastTarget = data.getCombatProcessor().getLastTarget();
                                                 if (target != null && lastTarget != null)
                                                 {
                                                     if (target != lastTarget)
                                                     {
                                                         data.getTargetLocations().clear();
                                                     }
                                                     Location location = target.getLocation();
                                                     data.getTargetLocations().add(new Pair<>(location, ticks));
                                                 }
                                             });
    }
}
