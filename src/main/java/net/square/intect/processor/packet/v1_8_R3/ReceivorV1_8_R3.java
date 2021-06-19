package net.square.intect.processor.packet.v1_8_R3;

import com.google.common.collect.Maps;
import io.github.retrooper.packetevents.packetwrappers.NMSPacket;
import io.github.retrooper.packetevents.packetwrappers.WrappedPacket;
import io.github.retrooper.packetevents.packetwrappers.play.in.blockdig.WrappedPacketInBlockDig;
import io.github.retrooper.packetevents.packetwrappers.play.in.clientcommand.WrappedPacketInClientCommand;
import io.github.retrooper.packetevents.packetwrappers.play.in.entityaction.WrappedPacketInEntityAction;
import io.github.retrooper.packetevents.packetwrappers.play.in.flying.WrappedPacketInFlying;
import io.github.retrooper.packetevents.packetwrappers.play.in.helditemslot.WrappedPacketInHeldItemSlot;
import io.github.retrooper.packetevents.packetwrappers.play.in.transaction.WrappedPacketInTransaction;
import io.github.retrooper.packetevents.packetwrappers.play.in.useentity.WrappedPacketInUseEntity;
import io.netty.channel.Channel;
import io.netty.channel.ChannelDuplexHandler;
import io.netty.channel.ChannelHandlerContext;
import io.netty.handler.codec.MessageToMessageDecoder;
import lombok.Getter;
import net.minecraft.server.v1_8_R3.*;
import net.square.intect.Intect;
import net.square.intect.checks.objectable.Check;
import net.square.intect.checks.objectable.IntectPacket;
import net.square.intect.processor.data.PlayerStorage;
import net.square.intect.processor.receivor.PacketReceivor;
import org.bukkit.craftbukkit.v1_8_R3.entity.CraftPlayer;
import org.bukkit.entity.Player;

import java.util.HashMap;
import java.util.List;

public class ReceivorV1_8_R3 implements PacketReceivor {

    @Getter
    private final Intect intect;

    private final HashMap<Player, Channel> channelHashMap = Maps.newHashMap();

    public ReceivorV1_8_R3(Intect intect) {
        this.intect = intect;
    }

    @Override
    public void inject(Player player) {
        CraftPlayer cPlayer = (CraftPlayer) player;
        Channel channel = cPlayer.getHandle().playerConnection.networkManager.channel;
        channel.pipeline().addAfter("decoder", "PacketInjector", new MessageToMessageDecoder<Packet<?>>() {
            @Override
            protected void decode(ChannelHandlerContext arg0, Packet<?> packet, List<Object> arg2) {
                arg2.add(packet);
                try {
                    readPacket(packet, player);
                } catch (Exception e) {
                    e.printStackTrace();
                }
            }
        });
        channelHashMap.put(player, channel);
    }

    public void readPacket(Packet<?> receive, Player player) {

        PlayerStorage data = PlayerStorage.storageHashMap.get(player);
        IntectPacket packet = new IntectPacket(IntectPacket.Direction.RECEIVE, receive, player);

        if (packet.getRawPacket() instanceof PacketPlayInEntityAction) {

            final WrappedPacketInEntityAction wrapper = new WrappedPacketInEntityAction(
                new NMSPacket(packet.getRawPacket()));

            data.getActionProcessor().handleEntityAction(wrapper);

        } else if (packet.getRawPacket() instanceof PacketPlayInBlockDig) {
            final WrappedPacketInBlockDig wrapper = new WrappedPacketInBlockDig(new NMSPacket(packet.getRawPacket()));

            data.getActionProcessor().handleBlockDig(wrapper);
        } else if (packet.getRawPacket() instanceof PacketPlayInClientCommand) {
            final WrappedPacketInClientCommand wrapper = new WrappedPacketInClientCommand(
                new NMSPacket(packet.getRawPacket()));

            data.getActionProcessor().handleClientCommand(wrapper);
        } else if (packet.getRawPacket() instanceof PacketPlayInBlockPlace) {
            data.getActionProcessor().handleBlockPlace();

        } else if (packet.getRawPacket() instanceof PacketPlayInHeldItemSlot) {
            final WrappedPacketInHeldItemSlot wrapper = new WrappedPacketInHeldItemSlot(
                new NMSPacket(packet.getRawPacket()));

            data.getActionProcessor().handleHeldItemSlot(wrapper);

        } else if (packet.getRawPacket() instanceof PacketPlayInCloseWindow) {
            data.getActionProcessor().handleCloseWindow();

        } else if (packet.getRawPacket() instanceof PacketPlayInUseEntity) {
            final WrappedPacketInUseEntity wrapper = new WrappedPacketInUseEntity(new NMSPacket(packet.getRawPacket()));
            data.getCombatProcessor().handleUseEntity(wrapper);

        } else if (packet.getRawPacket() instanceof PacketPlayInFlying) {
            final WrappedPacketInFlying wrapper = new WrappedPacketInFlying(new NMSPacket(packet.getRawPacket()));

            data.getActionProcessor().handleFlying();
            data.getCombatProcessor().handleFlying();

            if (wrapper.isPosition()) {
                data.getPositionProcessor()
                    .handle(wrapper.getX(), wrapper.getY(), wrapper.getZ(), wrapper.isOnGround());
            }
            if (wrapper.isLook()) {
                data.getRotationProcessor().handle(wrapper.getYaw(), wrapper.getPitch());
            }
        } else if (packet.getRawPacket() instanceof PacketPlayInArmAnimation) {

            data.getActionProcessor().handleArmAnimation();
            data.getCombatProcessor().handleArmAnimation();

        }

        for (Check check : data.getChecks()) {

            check.handle(new IntectPacket(IntectPacket.Direction.RECEIVE, receive, player));
        }
    }

    @Override
    public void uninject(Player player) {
        Channel channel = channelHashMap.get(player);
        if (channel.pipeline().get("PacketInjector") != null) {
            channel.pipeline().remove("PacketInjector");
        }
    }
}
