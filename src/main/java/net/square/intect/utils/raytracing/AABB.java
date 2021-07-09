package net.square.intect.utils.raytracing;

import org.bukkit.Location;

public class AABB
{
    private final Vec3D max;
    private final Vec3D min;

    /**
     * Creates a new instance from a minimum point and a maximum point.
     */
    public AABB(Vec3D min, Vec3D max)
    {
        this.min = min;
        this.max = max;
    }

    /**
     * Create a new AABB from a given block.
     *
     * @param block - the block.
     */
    public AABB(Location block)
    {
        this(Vec3D.fromLocation(block), Vec3D.fromLocation(block).add(Vec3D.UNIT_MAX));
    }

    /**
     * Calculates intersection with the given ray between a certain distance
     * interval.
     * <p>
     * Ray-box intersection is using IEEE numerical properties to ensure the
     * test is both robust and efficient, as described in:
     * <br>
     * <code>Amy Williams, Steve Barrus, R. Keith Morley, and Peter Shirley: "An
     * Efficient and Robust Ray-Box Intersection Algorithm" Journal of graphics
     * tools, 10(1):49-54, 2005</code>
     *
     * @param ray     incident ray
     * @param minDist
     * @param maxDist
     * @return intersection point on the bounding box (only the first is
     * returned) or null if no intersection
     */
    public Vec3D intersectsRay(Ray3D ray, float minDist, float maxDist)
    {
        Vec3D invDir = new Vec3D(1f / ray.dir.x, 1f / ray.dir.y, 1f / ray.dir.z);

        boolean signDirX = invDir.x < 0;
        boolean signDirY = invDir.y < 0;
        boolean signDirZ = invDir.z < 0;

        Vec3D bbox = signDirX ? max : min;
        double tmin = (bbox.x - ray.x) * invDir.x;
        bbox = signDirX ? min : max;
        double tmax = (bbox.x - ray.x) * invDir.x;
        bbox = signDirY ? max : min;
        double tymin = (bbox.y - ray.y) * invDir.y;
        bbox = signDirY ? min : max;
        double tymax = (bbox.y - ray.y) * invDir.y;

        if ((tmin > tymax) || (tymin > tmax))
        {
            return null;
        }
        if (tymin > tmin)
        {
            tmin = tymin;
        }
        if (tymax < tmax)
        {
            tmax = tymax;
        }

        bbox = signDirZ ? max : min;
        double tzmin = (bbox.z - ray.z) * invDir.z;
        bbox = signDirZ ? min : max;
        double tzmax = (bbox.z - ray.z) * invDir.z;

        if ((tmin > tzmax) || (tzmin > tmax))
        {
            return null;
        }
        if (tzmin > tmin)
        {
            tmin = tzmin;
        }
        if (tzmax < tmax)
        {
            tmax = tzmax;
        }
        if ((tmin < maxDist) && (tmax > minDist))
        {
            return ray.getPointAtDistance(tmin);
        }
        return null;
    }
}