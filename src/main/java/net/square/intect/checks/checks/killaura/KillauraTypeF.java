package net.square.intect.checks.checks.killaura;

import net.minecraft.server.v1_8_R3.PacketPlayInFlying;
import net.minecraft.server.v1_8_R3.PacketPlayInUseEntity;
import net.square.intect.checks.objectable.Check;
import net.square.intect.checks.objectable.CheckInfo;
import net.square.intect.checks.objectable.IntectPacket;
import net.square.intect.processor.data.PlayerStorage;

@CheckInfo(name = "Killaura", type = "F", description = "Checks for double hits", maxVL = 20)
public class KillauraTypeF extends Check {

    public KillauraTypeF(PlayerStorage data) {
        super(data);
    }

    private long lastAura = 0;

    private int count = 0;

    @Override
    public void handle(IntectPacket packet) {

        if (shouldBypass()) return;

        if (packet.getRawPacket() instanceof PacketPlayInUseEntity) {

            long delay = now() - lastAura;

            if (count > 2 && !getStorage().getCombatProcessor()
                .getTarget()
                .equals(getStorage().getCombatProcessor().getLastTarget())
                && delay < 10) {
                fail();
            }
            count = count + 1;
            lastAura = now();

        } else if (packet.getRawPacket() instanceof PacketPlayInFlying) {
            count = 0;
        }
    }
}
