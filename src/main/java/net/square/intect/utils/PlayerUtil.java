package net.square.intect.utils;

import net.square.intect.processor.custom.PositionProcessor;
import net.square.intect.processor.data.PlayerStorage;
import org.bukkit.Location;
import org.bukkit.Material;
import org.bukkit.World;
import org.bukkit.block.Block;
import org.bukkit.craftbukkit.v1_8_R3.entity.CraftPlayer;
import org.bukkit.entity.Entity;
import org.bukkit.entity.Player;
import org.bukkit.entity.Vehicle;
import org.bukkit.potion.PotionEffect;
import org.bukkit.potion.PotionEffectType;
import org.bukkit.util.Vector;

import java.util.LinkedList;
import java.util.List;

public class PlayerUtil
{

    @SuppressWarnings("unused")
    public int getPing(final Player player)
    {
        return ((CraftPlayer) player).getHandle().ping;
    }

    @SuppressWarnings("unused")
    public double getBaseSpeed(Player player)
    {
        return 0.36 + (getPotionLevel(player, PotionEffectType.SPEED) * 0.062f) + ((player.getWalkSpeed() - 0.2f)
            * 1.6f);
    }

    @SuppressWarnings("unused")
    public double getBaseGroundSpeed(Player player)
    {
        return 0.288 + (getPotionLevel(player, PotionEffectType.SPEED) * 0.062f) + ((player.getWalkSpeed() - 0.2f)
            * 1.6f);
    }

    @SuppressWarnings("unused")
    public static int getEffectLevel(final Player player, final PotionEffectType pet)
    {
        for (final PotionEffect pe : player.getActivePotionEffects())
        {
            if (pe.getType().getName().equalsIgnoreCase(pet.getName()))
            {
                return pe.getAmplifier() + 1;
            }
        }
        return 0;
    }

    public int getPotionLevel(final Player player, final PotionEffectType effect)
    {
        //noinspection deprecation
        final int effectId = effect.getId();

        if (!player.hasPotionEffect(effect)) return 0;

        for (PotionEffect potionEffect : player.getActivePotionEffects())
        {
            //noinspection deprecation
            if (potionEffect.getType().getId() == effectId)
            {
                int amplifier = potionEffect.getAmplifier();
                return amplifier + 1;
            }
        }
        return 1;
    }

    @SuppressWarnings("unused")
    public boolean shouldCheckJesus(final PlayerStorage data)
    {
        final boolean onWater = data.getPositionProcessor().isCollidingAtLocation(
            -0.001,
            material -> material.toString().contains("WATER"),
            PositionProcessor.CollisionType.ANY
        );

        final boolean hasEdgeCases = data.getPositionProcessor().isCollidingAtLocation(
            -0.001,
            material -> material == Material.CARPET || material == Material.WATER_LILY || material.isSolid(),
            PositionProcessor.CollisionType.ANY
        );

        return onWater && !hasEdgeCases;
    }

    /**
     * Gets the block in the given distance, If the block is null we immediately break to avoid issues
     *
     * @param player   The player
     * @param distance The distance
     * @return The block in the given distance, otherwise null
     */
    public static Block getLookingBlock(final Player player, final int distance)
    {
        final Location loc = player.getEyeLocation();

        final Vector v = loc.getDirection().normalize();

        for (int i = 1; i <= distance; i++)
        {
            loc.add(v);

            Block b = loc.getBlock();
            /*
            I'd recommend moving fiona's getBlock method in a seperate class
            So we can use it in instances like this.
             */
            if (b == null) break;

            if (b.getType() != Material.AIR) return b;
        }

        return null;
    }

    /**
     * Bukkit's getNearbyEntities method looks for all entities in all chunks
     * This is a lighter method and can also be used Asynchronously since we won't load any chunks
     *
     * @param location The location to scan for nearby entities
     * @param radius   The radius to expand
     * @return The entities within that radius
     * @author Nik
     */
    public static List<Entity> getEntitiesWithinRadius(final Location location, final double radius)
    {

        final double expander = 16.0D;

        final double x = location.getX();
        final double z = location.getZ();

        final int minX = (int) Math.floor((x - radius) / expander);
        final int maxX = (int) Math.floor((x + radius) / expander);

        final int minZ = (int) Math.floor((z - radius) / expander);
        final int maxZ = (int) Math.floor((z + radius) / expander);

        final World world = location.getWorld();

        List<Entity> entities = new LinkedList<>();

        for (int xVal = minX; xVal <= maxX; xVal++)
        {

            for (int zVal = minZ; zVal <= maxZ; zVal++)
            {

                if (!world.isChunkLoaded(xVal, zVal)) continue;

                for (Entity entity : world.getChunkAt(xVal, zVal).getEntities())
                {
                    //We have to do this due to stupidness
                    if (entity == null) continue;

                    //Make sure the entity is within the radius specified
                    if (entity.getLocation().distanceSquared(location) > radius * radius) continue;

                    entities.add(entity);
                }
            }
        }

        return entities;
    }

    @SuppressWarnings("unused")
    public static boolean isNearVehicle(final Player player)
    {
        for (final Entity entity : getEntitiesWithinRadius(player.getLocation(), 2))
        {
            if (entity instanceof Vehicle) return true;
        }

        return false;
    }
}
