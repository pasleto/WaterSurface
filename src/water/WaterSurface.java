package water;

import utils.Point3D;
import utils.WaterArray;

import java.util.ArrayList;
import java.util.List;

public class WaterSurface {
    private List<WaterArray> staticPoints = new ArrayList<>();
    private List<WaterArray> movedPoints = new ArrayList<>();
    private int size;
    private int density;

    WaterSurface(int size, int density) { //grid generation and init load in moved points (calm water)
        this.size = (size - 1) * density + 1; //count density points - example (50 points, 4 density points(holes) -> 49 holes between 0 and 50, 3 points per each hole => 50 + 3 * 49 = 197)
        this.density = density;
        double densityPoint = 1.0 / density; //count 1 / density points

        createPoints(size, densityPoint);
        loadPoints(staticPoints);
    }

    WaterSurface(List<WaterArray> pointList, int size, int density) { //repaint constructor - build new water surface with moved z-points
        this.staticPoints = pointList;
        this.size = size;
        this.density = density;

        loadPoints(pointList);
    }

    private void createPoints(int size, double densityPoint) { //point generating into static points list
        for (double x = -size / 2; x <= size / 2.0 - 1; x = x + densityPoint) {
            for (double y = -size / 2; y <= size / 2.0 - 1; y = y + densityPoint) {
                staticPoints.add(new WaterArray(new Point3D(x, y, 0.0), 0.0)); //points of grid generation
            }
        }
    }

    private void loadPoints(List<WaterArray> points) { //load points into moved points list
        for (WaterArray point : points) {
            movedPoints.add(new WaterArray(new Point3D(point.point.getX(), point.point.getY(), point.point.getZ() + point.value), point.value));
        }
    }

    public int getDensity() {
        return density;
    }

    public int getSize() {
        return size;
    }

    List<WaterArray> getMovedPoints() {
        return movedPoints;
    }

    public List<WaterArray> getStaticPoints() {
        return staticPoints;
    }

}