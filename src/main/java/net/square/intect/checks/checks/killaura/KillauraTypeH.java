package net.square.intect.checks.checks.killaura;

import io.github.retrooper.packetevents.packetwrappers.NMSPacket;
import io.github.retrooper.packetevents.packetwrappers.play.in.useentity.WrappedPacketInUseEntity;
import net.minecraft.server.v1_8_R3.PacketPlayInFlying;
import net.minecraft.server.v1_8_R3.PacketPlayInUseEntity;
import net.square.intect.checks.objectable.Check;
import net.square.intect.checks.objectable.CheckInfo;
import net.square.intect.checks.objectable.IntectPacket;
import net.square.intect.processor.data.PlayerStorage;

@CheckInfo(name = "Killaura", type = "H", description = "Checks for correct packet order", maxVL = 20)
public class KillauraTypeH extends Check
{
    public KillauraTypeH(PlayerStorage data)
    {
        super(data);
    }

    private boolean sent = false;
    private long lastFlying = 0;

    @Override
    public void handle(IntectPacket packet)
    {
        if (shouldBypass()) return;

        if (packet.getRawPacket() instanceof PacketPlayInFlying)
        {

            final long now = now();

            final long delay = now - lastFlying;

            if (sent)
            {
                if (delay > 40L && delay < 100L)
                {
                    if (increaseBuffer() > 3)
                    {
                        fail("sent packet incorrect", String.format("delay=%.4f", delay), 1);
                    }
                }
                else
                {
                    decreaseBufferBy(.015);
                }

                this.sent = false;
            }
            this.lastFlying = now;
        }
        else if (packet.getRawPacket() instanceof PacketPlayInUseEntity)
        {

            WrappedPacketInUseEntity wrapped = new WrappedPacketInUseEntity(new NMSPacket(packet.getRawPacket()));


            check:
            {
                if (wrapped.getAction() != WrappedPacketInUseEntity.EntityUseAction.ATTACK) break check;

                final long delay = now() - lastFlying;

                if (delay < 10)
                {
                    this.sent = true;
                }
            }
        }
    }
}
