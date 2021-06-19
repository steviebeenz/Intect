package net.square.intect.checks.checks.pattern;

import net.square.intect.checks.objectable.Check;
import net.square.intect.checks.objectable.CheckInfo;
import net.square.intect.checks.objectable.IntectPacket;
import net.square.intect.processor.custom.RotationProcessor;
import net.square.intect.processor.data.PlayerStorage;

@CheckInfo(name = "Pattern", type = "Liquid", maxVL = 5, description = "Checks for liquidbounce heuristics")
public class PatternTypeA extends Check {

    public PatternTypeA(PlayerStorage data) {
        super(data);
    }

    private int threshold = 0;

    @Override
    public void handle(IntectPacket packet) {

        if(shouldBypass()) return;

        RotationProcessor processor = getStorage().getRotationProcessor();

        double f = processor.getFinalSensitivity() * 0.6F + 0.2F;
        double gcd = f * f * f * 1.2F;

        double deltaYaw = processor.getDeltaYaw() % gcd;

        double deltaPitch = processor.getDeltaPitch() % gcd;

        double yaw = deltaYaw - getStorage().getLastLiquidYaw();
        double pitch = deltaPitch - getStorage().getLastLiquidPitch();

        debug(String.format("pitch=%.4f yaw=%.4f", Math.abs(pitch), Math.abs(yaw)));

        if (pitch > 10.0 && yaw > 0.0) {
            if(threshold++ > 1) {
                getPlayer().sendMessage("§c§lHAXX");
                getPlayer().sendMessage("§c§lHAXX");
                getPlayer().sendMessage("§c§lHAXX");
                getPlayer().sendMessage("§c§lHAXX");
                getPlayer().sendMessage("§c§lHAXX");
            }
        } else {
            threshold -= threshold > 0 ? 1 : 0;
        }

        //if (pitch >= 5.0 && yaw <= 5.0 && (System.currentTimeMillis() - storage.lastEntityHit) < 750) {
        //    if (storage.liquidThreshold++ >= 1) {
        //        this.markPlayer(player, 1, "KillAura", "moved like liquidbounce-heuristics", "L1");
        //    }
        //} else storage.liquidThreshold -= storage.liquidThreshold > 0 ? 1 : 0;

        getStorage().setLastLiquidYaw(deltaYaw);
        getStorage().setLastLiquidPitch(deltaPitch);
    }
}
