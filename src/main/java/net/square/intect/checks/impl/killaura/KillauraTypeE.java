package net.square.intect.checks.impl.killaura;

import io.github.retrooper.packetevents.packetwrappers.play.in.entityaction.WrappedPacketInEntityAction;
import net.square.intect.checks.objectable.Check;
import net.square.intect.checks.objectable.CheckInfo;
import net.square.intect.utils.objectable.IntectPacket;
import net.square.intect.processor.data.PlayerStorage;


@CheckInfo(name = "Killaura", type = "E", description = "Checks for re-sprint delta", maxVL = 20)
public class KillauraTypeE extends Check
{

    public KillauraTypeE(PlayerStorage data)
    {
        super(data);
    }

    private long lastStopSprinting = 0;

    @Override
    public void handle(IntectPacket packet)
    {

        if (shouldBypass()) return;

        if (packet.getRawPacket() instanceof WrappedPacketInEntityAction)
        {

            final WrappedPacketInEntityAction wrapped = (WrappedPacketInEntityAction) packet.getRawPacket();

            WrappedPacketInEntityAction.PlayerAction action = wrapped.getAction();

            if (action == WrappedPacketInEntityAction.PlayerAction.START_SPRINTING)
            {

                final long deltaAction = now() - lastStopSprinting;

                if (deltaAction < 40L)
                {
                    if (increaseBuffer() > 2)
                    {
                        fail("re-sprinted invalid", String.format("delta %d < 40", deltaAction), 1);
                    }
                }
                else
                {
                    decreaseBufferBy(0.5);
                }
            }
            if (action == WrappedPacketInEntityAction.PlayerAction.STOP_SPRINTING)
            {
                lastStopSprinting = now();
            }
        }
    }
}
