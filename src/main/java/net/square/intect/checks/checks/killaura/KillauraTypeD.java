package net.square.intect.checks.checks.killaura;

import net.square.intect.checks.objectable.Check;
import net.square.intect.checks.objectable.CheckInfo;
import net.square.intect.checks.objectable.IntectPacket;
import net.square.intect.processor.data.PlayerStorage;
import org.bukkit.event.EventHandler;
import org.bukkit.event.player.PlayerMoveEvent;

@CheckInfo(name = "Killaura", type = "D", description = "Invalid delta combat", maxVL = 20, bukkit = true)
public class KillauraTypeD extends Check {

    public KillauraTypeD(PlayerStorage data) {
        super(data);
    }

    private int threshold = 0;

    @EventHandler
    public void handle(PlayerMoveEvent event) {

        if (shouldBypass()) return;

        final float deltaYaw = getStorage().getRotationProcessor().getDeltaYaw();
        final float deltaPitch = getStorage().getRotationProcessor().getDeltaPitch();

        if (deltaPitch < 0.1 && deltaYaw > 3.5) {
            if (++threshold > 5) {

                threshold = 0;

                fail();
            }
        } else {
            threshold = ((threshold > 0) ? 1 : 0);
        }
    }

    @Override
    public void handle(IntectPacket packet) { }
}