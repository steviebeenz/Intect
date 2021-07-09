package net.square.intect.checks.checks.killaura;

import io.github.retrooper.packetevents.packetwrappers.NMSPacket;
import io.github.retrooper.packetevents.packetwrappers.play.in.flying.WrappedPacketInFlying;
import net.minecraft.server.v1_8_R3.PacketPlayInFlying;
import net.square.intect.checks.objectable.Check;
import net.square.intect.checks.objectable.CheckInfo;
import net.square.intect.checks.objectable.IntectPacket;
import net.square.intect.processor.data.PlayerStorage;
import net.square.intect.utils.PlayerUtil;
import org.bukkit.entity.Player;

@CheckInfo(name = "Killaura", type = "A", description = "Looking for invalid acceleration", maxVL = 20)
public class KillauraTypeA extends Check
{
    public KillauraTypeA(PlayerStorage data)
    {
        super(data);
    }

    @Override
    public void handle(IntectPacket packet)
    {

        if(shouldBypass()) return;

        if(!(packet.getRawPacket() instanceof PacketPlayInFlying)) {
            return;
        }

        WrappedPacketInFlying wrapped = new WrappedPacketInFlying(new NMSPacket(packet.getRawPacket()));

        if (wrapped.isLook() && getStorage().getCombatProcessor().getHitTicks() < 3)
        {

            final double deltaXZ = getStorage().getPositionProcessor().getDeltaXZ();
            final double lastDeltaXZ = getStorage().getPositionProcessor().getLastDeltaXZ();

            final double acceleration = Math.abs(deltaXZ - lastDeltaXZ);

            final boolean sprinting = getStorage().getActionProcessor().isSprinting();

            final boolean target = getStorage().getCombatProcessor().getTarget() != null &&
                getStorage().getCombatProcessor().getTarget() instanceof Player;

            final double baseSpeed = PlayerUtil.getBaseSpeed(getPlayer());

            final boolean invalid = acceleration < .0025 && sprinting && target && deltaXZ > baseSpeed;

            if (invalid)
            {
                if (increaseBuffer() > 5)
                {
                    fail("moved invalid", String.format("accel=%.4f dXZ=%.4f", acceleration, deltaXZ), 1);
                }
            }
            else
            {
                decreaseBufferBy(.45);
            }
        }
    }
}
