package utils;


public class Vec3D {
    private final double x, y, z;

    public Vec3D() {
        x = y = z = 0.0f;
    }

    public Vec3D(final double x, final double y, final double z) {
        this.x = x;
        this.y = y;
        this.z = z;
    }

    public Vec3D(final Vec3D v) {
        x = v.x;
        y = v.y;
        z = v.z;
    }

    public Vec3D(Point3D point) {
        x = point.getX();
        y = point.getY();
        z = point.getZ();
    }

    public double getX() {
        return x;
    }

    public double getY() {
        return y;
    }

    public double getZ() {
        return z;
    }

    public Vec3D cross(final Vec3D v) {
        return new Vec3D(y * v.z - z * v.y, z * v.x - x * v.z, x * v.y - y * v.x);
    }

}