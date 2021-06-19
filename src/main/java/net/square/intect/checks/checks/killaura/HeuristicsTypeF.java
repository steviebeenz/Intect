package net.square.intect.checks.checks.killaura;

import net.minecraft.server.v1_8_R3.PacketPlayInFlying;
import net.square.intect.checks.objectable.Check;
import net.square.intect.checks.objectable.CheckInfo;
import net.square.intect.checks.objectable.IntectPacket;
import net.square.intect.processor.data.PlayerStorage;

@CheckInfo(name = "Heuristics", type = "F", description = "Checks for pattern movement", maxVL = 20)
public class HeuristicsTypeF extends Check {

    public HeuristicsTypeF(PlayerStorage data) {
        super(data);
    }

    private int threshold = 0;

    @Override
    public void handle(IntectPacket packet) {

        if (shouldBypass()) return;

        if(packet.getRawPacket() instanceof PacketPlayInFlying) {
            final float customFloat = getStorage().getRotationProcessor().getDeltaYaw();
            final float deltaPitch = getStorage().getRotationProcessor().getDeltaPitch();

            final boolean invalid = (deltaPitch % 1 == 0 || customFloat % 1 == 0)
                && deltaPitch != 0 && customFloat != 0;

            if (invalid) {
                if (threshold++ > 3) {
                    fail();
                }
            } else {
                threshold -= threshold > 0 ? 1 : 0;
            }
        }
    }
}
