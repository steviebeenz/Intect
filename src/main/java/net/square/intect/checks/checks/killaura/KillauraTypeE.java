package net.square.intect.checks.checks.killaura;

import io.github.retrooper.packetevents.packetwrappers.NMSPacket;
import io.github.retrooper.packetevents.packetwrappers.play.in.entityaction.WrappedPacketInEntityAction;
import net.minecraft.server.v1_8_R3.PacketPlayInEntityAction;
import net.square.intect.checks.objectable.Check;
import net.square.intect.checks.objectable.CheckInfo;
import net.square.intect.checks.objectable.IntectPacket;
import net.square.intect.processor.data.PlayerStorage;


@CheckInfo(name = "Killaura", type = "E", description = "Checks for re-sprint delta", maxVL = 20)
public class KillauraTypeE extends Check {

    public KillauraTypeE(PlayerStorage data) {
        super(data);
    }

    private long lastStopSprinting = 0;

    private int count = 0;
    private int threshold = 0;

    @Override
    public void handle(IntectPacket packet) {

        if (shouldBypass()) return;

        if (packet.getRawPacket() instanceof PacketPlayInEntityAction) {

            final WrappedPacketInEntityAction wrapped = new WrappedPacketInEntityAction(
                new NMSPacket(packet.getRawPacket()));

            WrappedPacketInEntityAction.PlayerAction action = wrapped.getAction();

            if (action == WrappedPacketInEntityAction.PlayerAction.START_SPRINTING) {

                final long deltaAction = now() - lastStopSprinting;
                count = count + 1;

                if (deltaAction < 40L) {
                    if (threshold++ > 2) {
                        fail();
                        count = 0;
                    }
                } else {
                   threshold = threshold > 0 ? threshold - 1 : 0;
                }
            }
            if (action == WrappedPacketInEntityAction.PlayerAction.STOP_SPRINTING) {
                count = count + 1;
                lastStopSprinting = now();
            }
        }
    }
}
