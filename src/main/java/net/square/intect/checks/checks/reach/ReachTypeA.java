package net.square.intect.checks.checks.reach;

import io.github.retrooper.packetevents.packetwrappers.NMSPacket;
import io.github.retrooper.packetevents.packetwrappers.play.in.flying.WrappedPacketInFlying;
import io.github.retrooper.packetevents.packetwrappers.play.in.useentity.WrappedPacketInUseEntity;
import net.minecraft.server.v1_8_R3.AxisAlignedBB;
import net.minecraft.server.v1_8_R3.PacketPlayInFlying;
import net.minecraft.server.v1_8_R3.PacketPlayInUseEntity;
import net.square.intect.checks.objectable.Check;
import net.square.intect.checks.objectable.CheckInfo;
import net.square.intect.checks.objectable.IntectPacket;
import net.square.intect.processor.data.PlayerStorage;
import net.square.intect.utils.objectable.CustomLocation;
import net.square.intect.utils.raytracing.AABB;
import net.square.intect.utils.raytracing.Ray3D;
import net.square.intect.utils.raytracing.Vec3D;
import org.bukkit.craftbukkit.v1_8_R3.entity.CraftEntity;
import org.bukkit.entity.EntityType;
import org.bukkit.entity.Player;
import org.bukkit.util.Vector;

@CheckInfo(name = "Reach", type = "A", description = "Ray-box intersection check", maxVL = 10)
public class ReachTypeA extends Check
{
    public ReachTypeA(PlayerStorage data)
    {
        super(data);
    }

    private int threshold = 0;

    @Override
    public void handle(IntectPacket packet)
    {

        if (shouldBypass()) return;

        if (packet.getRawPacket() instanceof PacketPlayInUseEntity)
        {
            WrappedPacketInUseEntity wrapped = new WrappedPacketInUseEntity(new NMSPacket(packet.getRawPacket()));

            if (!(wrapped.getEntity() instanceof Player))
            {
                return;
            }

            Player target = ((Player) wrapped.getEntity());

            checkDistance(packet.getPlayer(), (CraftEntity) target);

        }
        else if (packet.getRawPacket() instanceof PacketPlayInFlying)
        {

            WrappedPacketInFlying packetPlayInFlying = new WrappedPacketInFlying(new NMSPacket(packet.getRawPacket()));

            PlayerStorage storage = getStorage();

            CustomLocation location = storage.getLastLocation();

            double x = packetPlayInFlying.getX();
            double y = packetPlayInFlying.getY() + 1.62D;
            double z = packetPlayInFlying.getZ();
            float yaw = packetPlayInFlying.getYaw();
            float pitch = packetPlayInFlying.getPitch();

            if (location == null)
            {
                storage.setLastLocation(new CustomLocation(x, y, z, yaw, pitch));
            }
            else
            {
                if (packetPlayInFlying.isPosition())
                {
                    location.setX(x);
                    location.setY(y);
                    location.setZ(z);
                }
                if (packetPlayInFlying.isLook())
                {
                    location.setYaw(yaw);
                    location.setPitch(pitch);
                }
            }
        }
    }

    int hitboxPoints = 0;

    private void checkDistance(Player player, CraftEntity entity)
    {

        net.minecraft.server.v1_8_R3.Entity handle = entity.getHandle();
        CustomLocation flyingPosition = getStorage().getLastLocation();

        AxisAlignedBB boundingBox = handle.getBoundingBox().grow(0.1D, 0.1D, 0.1D);
        Vec3D min = new Vec3D(boundingBox.a, boundingBox.b, boundingBox.c);
        Vec3D max = new Vec3D(boundingBox.d, boundingBox.e, boundingBox.f);
        AABB aabb = new AABB(min, max);

        if(flyingPosition == null) return;

        Vector direction = flyingPosition.getDirection();

        Ray3D ray = new Ray3D(
            new Vec3D(flyingPosition.getX(), flyingPosition.getY(), flyingPosition.getZ()),
            new Vec3D(direction.getX(), direction.getY(), direction.getZ())
        );

        Vec3D vec = aabb.intersectsRay(ray, 0, 6);

        if (entity.getType() != EntityType.PLAYER) return;

        Player target = (Player) entity;

        if (vec == null)
        {
            if (hitboxPoints++ > 5)
            {
                fail("attacked a player out of box", "isect=null", 1);
            }
            return;
        }
        else
        {
            hitboxPoints = 0;
        }

        double distance = Math.sqrt(
            Math.pow((flyingPosition.getX() - vec.x), 2) +
                Math.pow((flyingPosition.getY() - vec.y), 2) +
                Math.pow((flyingPosition.getZ() - vec.z), 2)
        );

        debug(String.format("dist=%.4f pvel=%.4f tvel=%.4f", distance, player.getVelocity().length(),
                            target.getVelocity().length()));

        if (distance > 3.8)
        {
            if (increaseBuffer() > 5)
            {
                fail("attacked a player from too far away", String.format("dist=%.4f blocks", distance), 1);
            }
        }
        else
        {
            decreaseBufferBy(1);
        }
    }
}
