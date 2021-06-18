package net.square.intect.checks.checks.killaura;

import net.minecraft.server.v1_8_R3.PacketPlayInUseEntity;
import net.square.intect.checks.objectable.Check;
import net.square.intect.checks.objectable.CheckInfo;
import net.square.intect.checks.objectable.IntectPacket;
import net.square.intect.processor.data.PlayerStorage;

@CheckInfo(name = "Killaura", type = "K", description = "Checks for open inventory", maxVL = 20)
public class KillauraTypeK extends Check {

    public KillauraTypeK(PlayerStorage data) {
        super(data);
    }

    @Override
    public void handle(IntectPacket packet) {

        if (shouldBypass()) return;

        if(packet.getRawPacket() instanceof PacketPlayInUseEntity) {
            if (getStorage().getActionProcessor().isInventory()) {
                fail();
            }
        }
    }
}
