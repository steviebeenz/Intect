package net.square.intect.utils.raytracing;

import com.google.common.base.Objects;
import org.bukkit.Location;
import org.bukkit.util.Vector;

public class Vec3D {
    /**
     * Point with the coordinate (1, 1, 1).
     */
    public static final Vec3D UNIT_MAX = new Vec3D(1, 1, 1);

    /**
     * X coordinate.
     */
    public final double x;
    /**
     * Y coordinate.
     */
    public final double y;
    /**
     * Z coordinate.
     */
    public final double z;

    /**
     * Creates a new vector with the given coordinates.
     *
     * @param x the x
     * @param y the y
     * @param z the z
     */
    public Vec3D(double x, double y, double z) {
        this.x = x;
        this.y = y;
        this.z = z;
    }

    /**
     * Creates a new vector with the coordinates of the given vector.
     *
     * @param v vector to copy.
     */
    public Vec3D(Vec3D v) {
        this.x = v.x;
        this.y = v.y;
        this.z = v.z;
    }

    /**
     * Construct a vector from a Bukkit location.
     *
     * @param loc - the Bukkit location.
     */
    public static Vec3D fromLocation(Location loc) {
        return new Vec3D(loc.getX(), loc.getY(), loc.getZ());
    }

    /**
     * Construct a copy of our immutable vector from Bukkit's mutable vector.
     *
     * @param v - Bukkit vector.
     * @return A copy of the given vector.
     */
    public static Vec3D fromVector(Vector v) {
        return new Vec3D(v.getX(), v.getY(), v.getZ());
    }

    /**
     * Add vector v and returns result as new vector.
     *
     * @param v vector to add
     * @return result as new vector
     */
    public final Vec3D add(Vec3D v) {
        return new Vec3D(this.x + v.x, this.y + v.y, this.z + v.z);
    }

    /**
     * Scales vector uniformly and returns result as new vector.
     *
     * @param s scale factor
     * @return new vector
     */
    public Vec3D scale(double s) {
        return new Vec3D(this.x * s, this.y * s, this.z * s);
    }

    /**
     * Normalizes the vector so that its magnitude = 1.
     *
     * @return The normalized vector.
     */
    public Vec3D normalize() {
        double mag = Math.sqrt(this.x * this.x + this.y * this.y + this.z * this.z);

        if (mag > 0)
            return scale(1.0 / mag);
        return this;
    }

    @Override
    public boolean equals(Object obj) {
        if (obj instanceof Vec3D) {
            final Vec3D v = (Vec3D) obj;
            return this.x == v.x && this.y == v.y && this.z == v.z;
        }
        return false;
    }

    @Override
    public int hashCode() {
        return Objects.hashCode(this.x, this.y, this.z);
    }

    public String toString() {
        return String.format("{x: %g, y: %g, z: %g}", this.x, this.y, this.z);
    }
}