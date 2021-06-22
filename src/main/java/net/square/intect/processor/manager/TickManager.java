package net.square.intect.processor.manager;

import lombok.Getter;
import net.square.intect.Intect;
import org.bukkit.Bukkit;
import org.bukkit.scheduler.BukkitTask;

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
    }
}
