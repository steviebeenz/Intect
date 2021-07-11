package net.square.intect.utils.objectable;

import io.github.retrooper.packetevents.packetwrappers.WrappedPacket;
import lombok.Data;
import net.minecraft.server.v1_8_R3.Packet;
import org.bukkit.entity.Player;

@Data
public class IntectPacket
{

    private final WrappedPacket rawPacket;
    private final Player player;

    private final Direction direction;

    public IntectPacket(Direction direction, WrappedPacket rawPacket, Player player)
    {
        this.direction = direction;
        this.rawPacket = rawPacket;
        this.player = player;
    }

    public boolean isReceiving()
    {
        return direction == Direction.RECEIVE;
    }

    public boolean isSending()
    {
        return direction == Direction.SEND;
    }

    public enum Direction
    {
        SEND,
        RECEIVE
    }
}
