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

    @Getter
    private int testCount = 0;

    public void fail()
    {

        CheckInfo checkInfo = getCheckInfo();
        testCount++;

        Intect.getIntect().getStorageManager()
            .getVerboseMode()
            .forEach(player ->
                         // &9&lIntect &8> &fSnxqe &7failed &fFly C &7VL[&9187&7]
                         player.sendMessage(ChatColor.translateAlternateColorCodes(
                             '&',
                             Intect.getIntect().getPrefix() + "§f" + storage.getPlayer()
                                 .getName() + " §7failed §f" + checkInfo.name() +
                                 " " + checkInfo.type() + " §7VL[§9" + testCount + "§7]"
                         )));

        if (testCount > checkInfo.maxVL() && !checkInfo.experimental())
        {

            Bukkit.broadcastMessage(
                Intect.getIntect().getPrefix() + "§f" + storage.getPlayer().getName() + " §7was removed for cheating.");

            Intect.getIntect()
                .getServer()
                .getScheduler()
                .runTask(Intect.getIntect(), () -> storage.getPlayer()
                    .kickPlayer(Intect.getIntect().getPrefix() + "Intect-AI (Heuristics)\n"
                                    + "" + checkInfo.name() + "(T" + checkInfo.type()
                                    + ")/" + UUID.randomUUID()));

            testCount = 0;
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
