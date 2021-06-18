package net.square.intect.checks.checks.killaura;

import net.square.intect.checks.objectable.Check;
import net.square.intect.checks.objectable.CheckInfo;
import net.square.intect.checks.objectable.IntectPacket;
import net.square.intect.processor.data.PlayerStorage;
import net.square.intect.utils.MathUtil;
import org.bukkit.event.EventHandler;
import org.bukkit.event.player.PlayerMoveEvent;

@CheckInfo(name = "Killaura", type = "D", description = "Checks for invalid victim", maxVL = 20, bukkit = true)
public class KillauraTypeE extends Check {

    public KillauraTypeE(PlayerStorage data) {
        super(data);
    }

    private int threshold = 0;

    @EventHandler
    public void handle(PlayerMoveEvent event) {

        if (shouldBypass()) return;

        final float deltaYaw = getStorage().getRotationProcessor().getDeltaYaw();
        final float deltaPitch = getStorage().getRotationProcessor().getDeltaPitch();

        final double expander = Math.pow(2.0, 24.0);

        if (deltaYaw == 0.0f || deltaPitch == 0.0f) return;

        final float gcd = (float) MathUtil.getVictim(
            (long) (deltaPitch * expander), (long) (getStorage().getRotationProcessor().getLastDeltaPitch() * expander));

        if (gcd < 131072.0f) {
            if (++threshold > 2) {
                threshold--;

                fail();
            }
        } else if (threshold > 0) {
            --threshold;
        }
    }

    @Override
    public void handle(IntectPacket packet) { }
}
