package net.square.intect.checks.checks.killaura;

import io.github.retrooper.packetevents.packetwrappers.NMSPacket;
import io.github.retrooper.packetevents.packetwrappers.play.in.useentity.WrappedPacketInUseEntity;
import net.minecraft.server.v1_8_R3.PacketPlayInFlying;
import net.minecraft.server.v1_8_R3.PacketPlayInUseEntity;
import net.square.intect.checks.objectable.Check;
import net.square.intect.checks.objectable.CheckInfo;
import net.square.intect.checks.objectable.IntectPacket;
import net.square.intect.processor.data.PlayerStorage;
import net.square.intect.utils.objectable.AABB;
import net.square.intect.utils.objectable.Ray;
import org.bukkit.entity.Entity;
import org.bukkit.entity.LivingEntity;

import java.util.ArrayList;
import java.util.List;

@CheckInfo(name = "Killaura", type = "A", description = "Makes a attack raytrace", maxVL = 20)
public class KillauraTypeA extends Check {

    public KillauraTypeA(PlayerStorage data) {
        super(data);
    }

    private boolean combat = false;

    private final List<Double> distances = new ArrayList<>();

    private long lastFlying = 0;

    private int attackRaytraceThreshold = 0;

    @Override
    public void handle(IntectPacket packet) {

        if (shouldBypass()) return;

        if (packet.getRawPacket() instanceof PacketPlayInUseEntity) {

            WrappedPacketInUseEntity packetPlayInUseEntity = new WrappedPacketInUseEntity(
                new NMSPacket(packet.getRawPacket()));

            Entity entity = packetPlayInUseEntity.getEntity();

            if (!(entity instanceof LivingEntity)) return;

            combat = true;

        } else if (packet.getRawPacket() instanceof PacketPlayInFlying.PacketPlayInPositionLook) {

            if (!combat) return;

            if (distances.size() >= 5) distances.remove(0);

            if (elapsed(System.nanoTime() / 1000000, lastFlying) <= 500) {

                combat = false;

                Ray ray = Ray.from(packet.getPlayer());
                double dist = AABB.from(getStorage()
                                            .getCombatProcessor().getTarget()).collidesD(ray,
                                                                                         0, 10
                );

                if (dist != -1) distances.add(dist);

                if (distances.size() >= 5) {

                    double total = 0;
                    double avgReach = 0;

                    for (int i = 0; i < distances.size(); i++) {
                        total += distances.get(i);
                        avgReach = total / distances.size();
                    }

                    double maxAvgReach = 4.5;
                    double maxDistance = 4.8;

                    if (avgReach >= maxAvgReach && dist >= maxDistance) {
                        if (++attackRaytraceThreshold > 1) {
                            fail();
                        }
                    } else
                        attackRaytraceThreshold = 0;
                }
            }
            lastFlying = (System.nanoTime() / 1000000);
        }
    }
}
