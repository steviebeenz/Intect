package net.square.intect.checks.checks.killaura;

import io.github.retrooper.packetevents.packetwrappers.NMSPacket;
import io.github.retrooper.packetevents.packetwrappers.play.in.useentity.WrappedPacketInUseEntity;
import net.minecraft.server.v1_8_R3.PacketPlayInUseEntity;
import net.square.intect.checks.objectable.Check;
import net.square.intect.checks.objectable.CheckInfo;
import net.square.intect.checks.objectable.IntectPacket;
import net.square.intect.processor.data.PlayerStorage;
import org.bukkit.entity.Entity;
import org.bukkit.entity.LivingEntity;
import org.bukkit.util.Vector;

@CheckInfo(name = "Killaura", type = "B", description = "Checks for combat angle", maxVL = 20)
public class KillauraTypeB extends Check {

    public KillauraTypeB(PlayerStorage data) {
        super(data);
    }

    private int combatAnalyticsThreshold = 0;

    @Override
    public void handle(IntectPacket packet) {

        if (shouldBypass()) return;

        if (packet.getRawPacket() instanceof PacketPlayInUseEntity) {

            WrappedPacketInUseEntity packetPlayInUseEntity = new WrappedPacketInUseEntity(
                new NMSPacket(packet.getRawPacket()));

            if (packetPlayInUseEntity.getAction() == WrappedPacketInUseEntity.EntityUseAction.ATTACK) {

                final Entity entity = packetPlayInUseEntity.getEntity();

                if (!(entity instanceof LivingEntity)) return;

                Vector vec = entity.getLocation().clone().toVector().setY(0.0)
                    .subtract(packet.getPlayer().getEyeLocation().clone().toVector().setY(0.0));

                float angle = packet.getPlayer().getEyeLocation().getDirection().angle(vec);

                if (angle > 2.0) {
                    if (combatAnalyticsThreshold++ > 1) {
                        fail();
                    }
                } else
                    combatAnalyticsThreshold -= combatAnalyticsThreshold > 0 ? 1 : 0;
            }
        }
    }
}
