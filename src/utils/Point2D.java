package utils;


public class Point2D {
    private final double x, y, w;

    public Point2D() {
        x = y = 0.0;
        w = 1.0;
    }

    public Point2D(final double x, final double y) {
        this.x = x;
        this.y = y;
        this.w = 1.0;
    }

    public Point2D(final double x, final double y, final double w) {
        this.x = x;
        this.y = y;
        this.w = w;
    }

    public Point2D(final Point2D p) {
        this.x = p.getX();
        this.y = p.getY();
        this.w = p.getW();
    }

    public Point2D(final Point3D p) {
        this.x = p.getX();
        this.y = p.getY();
        this.w = p.getW();
    }

    public double getX() {
        return x;
    }

    public double getY() {
        return y;
    }

    public double getW() {
        return w;
    }

}