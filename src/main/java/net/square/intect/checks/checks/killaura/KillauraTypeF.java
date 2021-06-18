package net.square.intect.checks.checks.killaura;

import net.square.intect.checks.objectable.Check;
import net.square.intect.checks.objectable.CheckInfo;
import net.square.intect.checks.objectable.IntectPacket;
import net.square.intect.processor.data.PlayerStorage;
import org.bukkit.Location;
import org.bukkit.event.EventHandler;
import org.bukkit.event.player.PlayerMoveEvent;

@CheckInfo(name = "Killaura", type = "F", description = "Checks for invalid tam", maxVL = 20, bukkit = true)
public class KillauraTypeF extends Check {

    public KillauraTypeF(PlayerStorage data) {
        super(data);
    }

    private int totalMoves = 0;
    private int totalAimMoves = 0;
    private int totalAimPosLook = 0;

    private int threshold = 0;

    @EventHandler
    public void handle(PlayerMoveEvent event) {

        if (shouldBypass()) return;

        totalMoves = totalMoves + 1;

        Location to = event.getTo();
        Location from = event.getFrom();

        if (to.getYaw() == from.getYaw() && to.getPitch() == from.getPitch() && to.distance(from) > 0.0) {
            totalAimMoves = totalAimMoves + 1;
        }

        if ((to.getYaw() != from.getYaw() || to.getPitch() != from.getPitch()) && to.distance(from) > 0.0) {
            totalAimPosLook = totalAimPosLook + 1;
        }

        if (totalMoves == 50) {
            if (totalAimPosLook <= 35) {
                if (++threshold > 10) {
                    fail();
                }
            } else {
                threshold -= ((threshold > 0) ? 1 : 0);
            }
            totalMoves = 0;
            totalAimMoves = 0;
            totalAimPosLook = 0;
        }
    }

    @Override
    public void handle(IntectPacket packet) {
    }
}
