package net.square.intect.processor.custom;

import lombok.Getter;
import net.square.intect.Intect;
import net.square.intect.processor.data.PlayerStorage;
import net.square.intect.utils.PlayerUtil;
import org.bukkit.Bukkit;
import org.bukkit.Location;
import org.bukkit.Material;
import org.bukkit.block.Block;
import org.bukkit.util.NumberConversions;

import java.util.ArrayList;
import java.util.Objects;
import java.util.concurrent.FutureTask;
import java.util.function.Predicate;

@Getter
public final class PositionProcessor
{

    private final PlayerStorage data;

    private double x, y, z,
        lastX, lastY, lastZ,
        deltaX, deltaY, deltaZ, deltaXZ,
        lastDeltaX, lastDeltaZ, lastDeltaY, lastDeltaXZ;

    private boolean onGround, lastOnGround, mathematicallyOnGround;

    public PositionProcessor(final PlayerStorage data)
    {
        this.data = data;
    }

    public void handle(final double x, final double y, final double z, final boolean onGround)
    {
        //FIX THIS TELEPORT SYSTEM.

        lastX = this.x;
        lastY = this.y;
        lastZ = this.z;
        this.lastOnGround = this.onGround;

        this.x = x;
        this.y = y;
        this.z = z;
        this.onGround = onGround;

        lastDeltaX = deltaX;
        lastDeltaY = deltaY;
        lastDeltaZ = deltaZ;
        lastDeltaXZ = deltaXZ;

        deltaX = this.x - lastX;
        deltaY = this.y - lastY;
        deltaZ = this.z - lastZ;
        deltaXZ = Math.hypot(deltaX, deltaZ);

        mathematicallyOnGround = y % 0.015625 == 0.0;
    }

    public boolean isCollidingAtLocation(double drop, Predicate<Material> predicate, CollisionType collisionType)
    {
        final ArrayList<Material> materials = new ArrayList<>();

        for (double x = -0.3; x <= 0.3; x += 0.3)
        {
            for (double z = -0.3; z <= 0.3; z += 0.3)
            {
                final Material material = Objects.requireNonNull(
                    getBlock(data.getPlayer().getLocation().clone().add(x, drop, z))).getType();
                if (material != null)
                {
                    materials.add(material);
                }
            }
        }

        if (collisionType == CollisionType.ALL)
        {
            for (Material material : materials)
            {
                if (!predicate.test(material))
                {
                    return false;
                }
            }
            return true;
        }
        else
        {
            return materials.stream().allMatch(predicate);
        }
    }

    //Taken from Fiona. If you have anything better, please let me know, thanks.
    public Block getBlock(final Location location)
    {
        if (location.getWorld().isChunkLoaded(location.getBlockX() >> 4, location.getBlockZ() >> 4))
        {
            return location.getBlock();
        }
        else
        {
            FutureTask<Block> futureTask = new FutureTask<>(() ->
                                                            {
                                                                location.getWorld()
                                                                    .loadChunk(location.getBlockX() >> 4,
                                                                               location.getBlockZ() >> 4);
                                                                return location.getBlock();
                                                            });
            Bukkit.getScheduler().runTask(Intect.getIntect(), futureTask);
            try
            {
                return futureTask.get();
            } catch (final Exception exception)
            {
                exception.printStackTrace();
            }
            return null;
        }
    }

    public enum CollisionType
    {
        ANY, ALL
    }
}