package net.square.intect.checks.objectable;

import com.google.common.collect.Lists;
import lombok.Getter;
import lombok.Setter;
import net.square.intect.Intect;
import net.square.intect.processor.data.PlayerStorage;
import org.bukkit.Bukkit;
import org.bukkit.ChatColor;
import org.bukkit.GameMode;
import org.bukkit.entity.Player;
import org.bukkit.event.Listener;

import java.util.List;

@Getter
@Setter
public abstract class Check implements IntectHandler, Listener
{

    private PlayerStorage storage;

    public Check(final PlayerStorage data)
    {
        this.storage = data;
    }

    public abstract void handle(final IntectPacket packet);

    @Getter
    private final List<Player> debugMode = Lists.newArrayList();

    CheckInfo checkInfo = getCheckInfo();

    @Getter
    private int verbose = 0;

    private double buffer = 0.0;

    private boolean kicked = false;

    public void fail(String message, String information, int points)
    {

        if(kicked) return;

        verbose = verbose + points;

        Player currentPlayer = getPlayer();

        if (currentPlayer == null) return;

        Intect intect = Intect.getIntect();

        final String msg = information == null ? "" : "(" + information + ")";

        for (Player player1 : intect.getStorageManager()
            .getVerboseMode())
        {
            player1.sendMessage(ChatColor.translateAlternateColorCodes(
                '&',
                String.format("%s§7Verbose: %s %s and failed %s %s(+%d - %d)", intect.getPrefix(),
                              currentPlayer.getName(), message, checkInfo.name(), msg, points, verbose)
            ));
        }

        if (verbose >= checkInfo.maxVL() && !checkInfo.experimental())
        {
            kicked = true;

            intect.getServer().getScheduler().runTask(intect, () ->
            {
                for (Player player : Bukkit.getOnlinePlayers())
                {
                    if (player.hasPermission("intect.admin.notify"))
                    {
                        player.sendMessage(intect.getPrefix() + "§c§lNotify §c" + currentPlayer.getName()
                                               + " §7has been removed for attacking suspiciously");
                    }
                }
                storage.getPlayer()
                    .kickPlayer(intect.getPrefix() + "§7Attacking suspiciously");
            });
        }
    }

    public double increaseBuffer() {
        return buffer = Math.min(10000, buffer + 1);
    }

    public void decreaseBufferBy(final double amount) {
        buffer = Math.max(0, buffer - amount);
    }

    public CheckInfo getCheckInfo()
    {
        if (this.getClass().isAnnotationPresent(CheckInfo.class))
        {
            return this.getClass().getAnnotation(CheckInfo.class);
        }
        else
        {
            System.err.println(
                "CheckInfo annotation hasn't been added to the class " + this.getClass().getSimpleName() + ".");
        }
        return null;
    }

    public void debug(String message)
    {

        if (debugMode.isEmpty()) return;

        if (debugMode.contains(storage.getPlayer()))
        {
            storage.getPlayer().sendMessage(message);
        }
    }

    public Player getPlayer()
    {
        return storage.getPlayer();
    }

    public boolean shouldBypass()
    {
        PlayerStorage storage = getStorage();
        return (now() - getStorage().getCombatProcessor().getLastHit()) > 750 ||
            storage.getPlayer().getGameMode() == GameMode.CREATIVE
            || storage.getPlayer().getGameMode() == GameMode.SPECTATOR;
    }

    public long now()
    {
        return System.currentTimeMillis();
    }

    @SuppressWarnings("unused")
    public long elapsed(long now, long start)
    {
        return now - start;
    }

    @Override
    public boolean isEnabled()
    {
        return true;
    }
}
