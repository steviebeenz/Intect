package net.square.intect.processor.packet.v1_14_R1;

import com.google.common.collect.Maps;
import io.github.retrooper.packetevents.packetwrappers.NMSPacket;
import io.github.retrooper.packetevents.packetwrappers.play.in.blockdig.WrappedPacketInBlockDig;
import io.github.retrooper.packetevents.packetwrappers.play.in.clientcommand.WrappedPacketInClientCommand;
import io.github.retrooper.packetevents.packetwrappers.play.in.entityaction.WrappedPacketInEntityAction;
import io.github.retrooper.packetevents.packetwrappers.play.in.flying.WrappedPacketInFlying;
import io.github.retrooper.packetevents.packetwrappers.play.in.helditemslot.WrappedPacketInHeldItemSlot;
import io.github.retrooper.packetevents.packetwrappers.play.in.useentity.WrappedPacketInUseEntity;
import io.netty.channel.Channel;
import io.netty.channel.ChannelHandlerContext;
import io.netty.handler.codec.MessageToMessageDecoder;
import lombok.Getter;
import net.minecraft.server.v1_14_R1.*;
import net.square.intect.Intect;
import net.square.intect.checks.objectable.Check;
import net.square.intect.processor.custom.custom.WrappedPacketInArmAnimation;
import net.square.intect.processor.data.PlayerStorage;
import net.square.intect.processor.receivor.PacketReceivor;
import net.square.intect.utils.objectable.IntectPacket;
import org.bukkit.craftbukkit.v1_14_R1.entity.CraftPlayer;
import org.bukkit.entity.Player;

import java.util.HashMap;
import java.util.List;

public class ReceivorV1_14_R1
implements PacketReceivor
{
    @Getter
    private final Intect intect;

    private final HashMap<Player, Channel> channelHashMap = Maps.newHashMap();

    public ReceivorV1_14_R1(Intect intect)
    {
        this.intect = intect;
    }

    @Override
    public void inject(Player player)
    {
        //noinspection DuplicatedCode
        CraftPlayer cPlayer = (CraftPlayer) player;
        Channel channel = cPlayer.getHandle().playerConnection.networkManager.channel;
        channel.pipeline().addAfter("decoder", "PacketInjector", new MessageToMessageDecoder<Packet<?>>()
        {
            @Override
            protected void decode(ChannelHandlerContext arg0, Packet<?> packet, List<Object> arg2)
            {
                arg2.add(packet);
                try
                {
                    readPacket(packet, player);
                } catch (Exception e)
                {
                    e.printStackTrace();
                }
            }
        });
        channelHashMap.put(player, channel);
    }

    public void readPacket(@SuppressWarnings("rawtypes") Packet packet, Player player)
    {

        //noinspection DuplicatedCode
        PlayerStorage data = PlayerStorage.storageHashMap.get(player);

        if (packet instanceof PacketPlayInEntityAction)
        {
            WrappedPacketInEntityAction wrapper = new WrappedPacketInEntityAction(new NMSPacket(packet));
            data.getActionProcessor().handleEntityAction(wrapper);

            for (Check check : data.getChecks())
            {
                check.handle(new IntectPacket(IntectPacket.Direction.RECEIVE, wrapper, player));
            }
        }
        else if (packet instanceof PacketPlayInBlockDig)
        {
            data.getActionProcessor().handleBlockDig(new WrappedPacketInBlockDig(new NMSPacket(packet)));
        }
        else if (packet instanceof PacketPlayInClientCommand)
        {
            data.getActionProcessor().handleClientCommand(new WrappedPacketInClientCommand(new NMSPacket(packet)));
        }
        else if (packet instanceof PacketPlayInBlockPlace)
        {
            data.getActionProcessor().handleBlockPlace();
        }
        else if (packet instanceof PacketPlayInHeldItemSlot)
        {
            data.getActionProcessor().handleHeldItemSlot(new WrappedPacketInHeldItemSlot(new NMSPacket(packet)));
        }
        else if (packet instanceof PacketPlayInCloseWindow)
        {
            data.getActionProcessor().handleCloseWindow();
        }
        else if (packet instanceof PacketPlayInUseEntity)
        {
            WrappedPacketInUseEntity wrapper = new WrappedPacketInUseEntity(new NMSPacket(packet));
            data.getCombatProcessor().handleUseEntity(wrapper);


            for (Check check : data.getChecks())
            {
                check.handle(new IntectPacket(IntectPacket.Direction.RECEIVE, wrapper, player));
            }
        }
        else if (packet instanceof PacketPlayInFlying)
        {
            final WrappedPacketInFlying wrapper = new WrappedPacketInFlying(new NMSPacket(packet));

            data.getActionProcessor().handleFlying();
            data.getCombatProcessor().handleFlying();

            if (wrapper.isMoving())
            {
                data.getPositionProcessor()
                    .handle(wrapper.getPosition().getX(), wrapper.getPosition().getY(), wrapper.getPosition().getZ(),
                            wrapper.isOnGround());
            }
            if (wrapper.isRotating())
            {
                data.getRotationProcessor().handle(wrapper.getYaw(), wrapper.getPitch());
            }

            for (Check check : data.getChecks())
            {
                check.handle(new IntectPacket(IntectPacket.Direction.RECEIVE, wrapper, player));
            }
        }
        else if (packet instanceof PacketPlayInArmAnimation)
        {

            data.getActionProcessor().handleArmAnimation();
            data.getCombatProcessor().handleArmAnimation();

            for (Check check : data.getChecks())
            {
                check.handle(new IntectPacket(IntectPacket.Direction.RECEIVE, new WrappedPacketInArmAnimation(), player));
            }
        }
    }

    @Override
    public void uninject(Player player)
    {
        Channel channel = channelHashMap.get(player);

        if (channel == null) return;

        if (channel.pipeline().get("PacketInjector") != null)
        {
            channel.pipeline().remove("PacketInjector");
        }
    }

    @Override
    public String getName()
    {
        return "v1_14_R1";
    }
}
