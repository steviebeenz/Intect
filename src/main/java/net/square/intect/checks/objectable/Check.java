package net.square.intect.checks.objectable;

import com.google.common.collect.Lists;
import lombok.Getter;
import lombok.Setter;
import net.square.intect.Intect;
import net.square.intect.processor.data.PlayerStorage;
import org.bukkit.Bukkit;
import org.bukkit.ChatColor;
import org.bukkit.GameMode;
import org.bukkit.craftbukkit.v1_8_R3.entity.CraftPlayer;
import org.bukkit.entity.Player;
import org.bukkit.event.Listener;

import java.util.List;
import java.util.UUID;

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

    public void fail()
    {
        verbose++;

        Player currentPlayer = getPlayer();

        if (currentPlayer == null) return;

        Intect intect = Intect.getIntect();

        intect.getService().submit(() -> intect
            .getMySQLManager()
            .createLog(currentPlayer.getUniqueId(), currentPlayer.getName(), checkInfo.name(),
                       verbose,
                       ((CraftPlayer) currentPlayer).getHandle().ping));

        intect.getStorageManager()
            .getVerboseMode()
            .forEach(player ->
                         player.sendMessage(ChatColor.translateAlternateColorCodes(
                             '&',
                             intect.getPrefix() + "§f" + currentPlayer
                                 .getName() + " §7failed §f" + checkInfo.name() +
                                 " " + checkInfo.maxVL() + checkInfo.type() + " §7VL[§9" + verbose + "§7]"
                         )));

        if (verbose > checkInfo.maxVL() && !checkInfo.experimental())
        {

            Bukkit.broadcastMessage(
                intect.getPrefix() + "§f" + currentPlayer.getName() + " §7was removed for cheating.");

            intect
                .getServer()
                .getScheduler()
                .runTask(intect, () -> storage.getPlayer()
                    .kickPlayer(intect.getPrefix() + "Intect-AI (Combat)\n"
                                    + "" + checkInfo.name() + "(T" + checkInfo.type()
                                    + ")/" + UUID.randomUUID()));
        }
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
