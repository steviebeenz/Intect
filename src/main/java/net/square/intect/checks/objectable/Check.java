package net.square.intect.checks.objectable;

import lombok.Getter;
import lombok.Setter;
import net.square.intect.Intect;
import net.square.intect.processor.custom.PositionProcessor;
import net.square.intect.processor.data.PlayerStorage;
import org.bukkit.ChatColor;
import org.bukkit.GameMode;
import org.bukkit.Location;
import org.bukkit.event.Listener;

import java.util.UUID;

@Getter
@Setter
public abstract class Check implements IntectHandler, Listener {

    private PlayerStorage storage;

    public Check(final PlayerStorage data) {
        this.storage = data;
    }

    public abstract void handle(final IntectPacket packet);

    @Getter
    private int testCount = 0;

    public void fail() {

        CheckInfo checkInfo = getCheckInfo();
        testCount++;

        Intect.getIntect()
            .getVerboseMode()
            .forEach(player ->
                         player.sendMessage(ChatColor.translateAlternateColorCodes(
                             '&',
                             Intect.getIntect().getPrefix() + "§7"+ storage.getPlayer()
                                 .getName() + " failed §c" + checkInfo.name()
                                 + "§7(Vl:"+testCount+")"
                         )));

        PositionProcessor positionProcessor = storage.getPositionProcessor();
        storage.getPlayer()
            .teleport(new Location(storage.getPlayer().getWorld(), positionProcessor.getX(), positionProcessor.getY(),
                                   positionProcessor.getZ()
            ));

        if (testCount > checkInfo.maxVL()) {

            Intect.getIntect().getServer().getOnlinePlayers().forEach(player -> {
                if (player.hasPermission("intect.admin.verbose")) {
                    player.sendMessage(Intect.getIntect().getPrefix()
                                           + "§c§lINFO §c" + storage.getPlayer().getName()
                                           + " §7is attacking suspiciously");
                }
            });

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

    public CheckInfo getCheckInfo() {
        if (this.getClass().isAnnotationPresent(CheckInfo.class)) {
            return this.getClass().getAnnotation(CheckInfo.class);
        } else {
            System.err.println(
                "CheckInfo annotation hasn't been added to the class " + this.getClass().getSimpleName() + ".");
        }
        return null;
    }

    public boolean shouldBypass() {
        PlayerStorage storage = getStorage();
        return (now() - getStorage().getCombatProcessor().getLastHit()) > 750 ||
            storage.getPlayer().getGameMode() == GameMode.CREATIVE
            || storage.getPlayer().getGameMode() == GameMode.SPECTATOR;
    }

    public long now() {
        return System.currentTimeMillis();
    }

    public long elapsed(long now, long start) {
        return now - start;
    }

    @Override
    public boolean isEnabled() {
        return true;
    }
}