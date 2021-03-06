package net.square.intect.checks.impl.killaura;

import io.github.retrooper.packetevents.packetwrappers.play.in.useentity.WrappedPacketInUseEntity;
import net.square.intect.checks.objectable.Check;
import net.square.intect.checks.objectable.CheckInfo;
import net.square.intect.utils.objectable.IntectPacket;
import net.square.intect.processor.data.PlayerStorage;
import org.bukkit.entity.Entity;
import org.bukkit.entity.LivingEntity;
import org.bukkit.util.Vector;

@CheckInfo(name = "Killaura", type = "B", description = "Checks for combat angle", maxVL = 20)
public class KillauraTypeB extends Check
{

    public KillauraTypeB(PlayerStorage data)
    {
        super(data);
    }

    @Override
    public void handle(IntectPacket packet)
    {

        if (shouldBypass()) return;

        if (packet.getRawPacket() instanceof WrappedPacketInUseEntity)
        {

            WrappedPacketInUseEntity packetPlayInUseEntity = (WrappedPacketInUseEntity) packet.getRawPacket();

            if (packetPlayInUseEntity.getAction() == WrappedPacketInUseEntity.EntityUseAction.ATTACK)
            {

                final Entity entity = packetPlayInUseEntity.getEntity();

                if (!(entity instanceof LivingEntity)) return;

                Vector vec = entity.getLocation().clone().toVector().setY(0.0)
                    .subtract(packet.getPlayer().getEyeLocation().clone().toVector().setY(0.0));

                float angle = packet.getPlayer().getEyeLocation().getDirection().angle(vec);

                if (angle > 2.0)
                {
                    if (increaseBuffer() > 1)
                    {
                        fail("attacked a player out of angle", String.format("ang %.4f", angle), 1);
                    }
                }
                else
                {
                    decreaseBufferBy(0.25);
                }
            }
        }
    }
}
