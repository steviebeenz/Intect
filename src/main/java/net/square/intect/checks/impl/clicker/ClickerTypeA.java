package net.square.intect.checks.impl.clicker;

import io.github.retrooper.packetevents.packetwrappers.play.in.flying.WrappedPacketInFlying;
import io.github.retrooper.packetevents.packetwrappers.play.in.useentity.WrappedPacketInUseEntity;
import net.square.intect.checks.objectable.Check;
import net.square.intect.checks.objectable.CheckInfo;
import net.square.intect.utils.objectable.IntectPacket;
import net.square.intect.processor.data.PlayerStorage;

@CheckInfo(name = "Clicker", type = "A", description = "Max cps check", maxVL = 20, experimental = true)
public class ClickerTypeA
    extends Check
{
    public ClickerTypeA(PlayerStorage data)
    {
        super(data);
    }

    private int ticks = 0, cps = 0;
    private int maxCPS = 20;

    @Override
    public void handle(IntectPacket packet)
    {
        if (shouldBypass()) return;

        if (packet.getRawPacket() instanceof WrappedPacketInFlying)
        {
            if (ticks++ >= 20)
            {
                if (cps > maxCPS)
                {
                    fail("clicking too fast", String.format("cps=%d", cps), 1);
                }
                ticks = cps = 0;
            }
        }
        else if (packet.getRawPacket() instanceof WrappedPacketInUseEntity)
        {
            if (((WrappedPacketInUseEntity) packet.getRawPacket()).getAction()
                == WrappedPacketInUseEntity.EntityUseAction.ATTACK)
            {
                cps++;
            }
        }
    }
}
