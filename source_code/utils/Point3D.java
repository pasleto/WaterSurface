package utils;


public class Point3D {
    private final double x, y, z, w;

    public Point3D() {
        x = y = z = 0.0;
        w = 1.0;
    }

    public Point3D(final double x, final double y, final double z) {
        this.x = x;
        this.y = y;
        this.z = z;
        this.w = 1.0;
    }

    public Point3D(final double x, final double y, final double z, final double w) {
        this.x = x;
        this.y = y;
        this.z = z;
        this.w = w;
    }

    public Point3D(final Vec3D v) {
        this.x = v.getX();
        this.y = v.getY();
        this.z = v.getZ();
        this.w = 1.0;
    }

    public Point3D(final Point3D p) {
        this.x = p.x;
        this.y = p.y;
        this.z = p.z;
        this.w = p.w;
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

    public double getW() {
        return w;
    }

}