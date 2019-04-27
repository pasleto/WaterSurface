package water;

import com.jogamp.opengl.GL2;
import com.jogamp.opengl.GLAutoDrawable;
import renderer.Renderer;
import utils.Point3D;
import utils.WaterArray;
import utils.Vec3D;

import java.util.ArrayList;
import java.util.List;

public class WaterMoving {
    private WaterSurface waterSurface;
    private Renderer renderer;

    public WaterMoving(int size, int density, Renderer renderer) {
        this.waterSurface = new WaterSurface(size, density);
        this.renderer = renderer;
    }

    public void resetScene() { //reset to default
        List<WaterArray> resetPointsList = new ArrayList<>();

        for (int i = 0; i < waterSurface.getStaticPoints().size(); i++) {
            resetPointsList.add(new WaterArray(new Point3D(waterSurface.getStaticPoints().get(i).point.getX(), waterSurface.getStaticPoints().get(i).point.getY(), 0.0), 0.0));
        }
        this.waterSurface = new WaterSurface(resetPointsList, waterSurface.getSize(), waterSurface.getDensity());
    }

    public void drawSurfaceLevel(GLAutoDrawable glAutoDrawable) { //draw of grid
        for (int i = 0; i < waterSurface.getStaticPoints().size() - waterSurface.getSize(); i++) { //not draw last triangles line (index out of bound)
            if ((i + 1) % waterSurface.getSize() != 0) { //move to next line
                if (!renderer.getWaterSurfaceFill()) {
                    drawWireFrame(i, glAutoDrawable);
                }
                if (renderer.getWaterSurfaceFill()) {
                    drawFilledSurface(i, glAutoDrawable);
                }
            }
        }
        waterSurfaceMove(waterSurface);
    }

    private void point(Point3D point, GLAutoDrawable glAutoDrawable) { //point3d to vertex3d
        GL2 gl = glAutoDrawable.getGL().getGL2();
        gl.glVertex3d(point.getX(), point.getY(), point.getZ());
    }

    private void pointNormal(Vec3D vector, GLAutoDrawable glAutoDrawable) {
        GL2 gl = glAutoDrawable.getGL().getGL2();
        gl.glNormal3d(vector.getX(), vector.getY(), vector.getZ());
    }

    private Vec3D vectorNormal(int i) {
        List<WaterArray> points = waterSurface.getMovedPoints();

        if (i < waterSurface.getSize() || i % waterSurface.getSize() == 0 || (i + 1) % waterSurface.getSize() == 0 || i > waterSurface.getStaticPoints().size() - waterSurface.getSize()) {
            return new Vec3D(0, 0, 1);
        } else {
            Point3D a = points.get(i - 1).point;
            Point3D b = points.get(i + 1).point;
            Point3D c = points.get(i - waterSurface.getSize()).point;
            Point3D d = points.get(i + waterSurface.getSize()).point;

            Vec3D vec1 = new Vec3D(a.getX() - b.getX(), a.getY() - b.getY(), a.getZ() - b.getZ());
            Vec3D vec2 = new Vec3D(c.getX() - d.getX(), c.getY() - d.getY(), c.getZ() - d.getZ());

            return vec2.cross(vec1);
        }
    }

    private void drawWireFrame(int i, GLAutoDrawable glAutoDrawable) { //wireFrame draw
        GL2 gl = glAutoDrawable.getGL().getGL2();
        gl.glPolygonMode(GL2.GL_FRONT, GL2.GL_LINE);
        gl.glPolygonMode(GL2.GL_BACK, GL2.GL_NONE);

        point(waterSurface.getMovedPoints().get(i + waterSurface.getSize()).point, glAutoDrawable);
        point(waterSurface.getMovedPoints().get(i).point, glAutoDrawable);
        point(waterSurface.getMovedPoints().get(i + 1 + waterSurface.getSize()).point, glAutoDrawable);
        point(waterSurface.getMovedPoints().get(i).point, glAutoDrawable);
        point(waterSurface.getMovedPoints().get(i + 1).point, glAutoDrawable);
        point(waterSurface.getMovedPoints().get(i + waterSurface.getSize() + 1).point, glAutoDrawable);
    }

    private void drawFilledSurface(int i, GLAutoDrawable glAutoDrawable) { //filledFrame draw
        GL2 gl = glAutoDrawable.getGL().getGL2();
        gl.glPolygonMode(GL2.GL_FRONT, GL2.GL_LINE);
        gl.glPolygonMode(GL2.GL_BACK, GL2.GL_NONE);

        pointNormal(vectorNormal(i), glAutoDrawable);
        point(waterSurface.getMovedPoints().get(i).point, glAutoDrawable);
        pointNormal(vectorNormal(i + waterSurface.getSize()), glAutoDrawable);
        point(waterSurface.getMovedPoints().get(i + waterSurface.getSize()).point, glAutoDrawable);
        pointNormal(vectorNormal(i + 1 + waterSurface.getSize()), glAutoDrawable);
        point(waterSurface.getMovedPoints().get(i + 1 + waterSurface.getSize()).point, glAutoDrawable);
        pointNormal(vectorNormal(i + 1), glAutoDrawable);
        point(waterSurface.getMovedPoints().get(i + 1).point, glAutoDrawable);
        pointNormal(vectorNormal(i), glAutoDrawable);
        point(waterSurface.getMovedPoints().get(i).point, glAutoDrawable);
        pointNormal(vectorNormal(i + waterSurface.getSize() + 1), glAutoDrawable);
        point(waterSurface.getMovedPoints().get(i + waterSurface.getSize() + 1).point, glAutoDrawable);
    }

    private void waterSurfaceMove(WaterSurface waterSurface) {
        List<WaterArray> points = new ArrayList<>();

        for (int i = 0; i < waterSurface.getMovedPoints().size(); i++) {
            if (i < waterSurface.getSize() || i % waterSurface.getSize() == 0 || (i + 1) % waterSurface.getSize() == 0 || i > waterSurface.getMovedPoints().size() - waterSurface.getSize()) {
                if (i == 0 || i == waterSurface.getSize() - 1 || i == waterSurface.getMovedPoints().size() - waterSurface.getSize() || i == waterSurface.getMovedPoints().size() - 1) {
                    //left top corner
                    if (i == 0) {
                        if (renderer.getEdgesLock()) {
                            points.add(new WaterArray(new Point3D(waterSurface.getMovedPoints().get(i).point.getX(), waterSurface.getMovedPoints().get(i).point.getY(), waterSurface.getMovedPoints().get(i).point.getZ() * renderer.getEdgesDamping()), 0.0));
                        } else {
                            points.add(new WaterArray(new Point3D(waterSurface.getMovedPoints().get(i).point.getX(), waterSurface.getMovedPoints().get(i).point.getY(), waterSurface.getMovedPoints().get(i).point.getZ()),
                                    (waterSurface.getMovedPoints().get(i).value * renderer.getWaterDamping() + ((waterSurface.getMovedPoints().get(i + 1).point.getZ() - waterSurface.getMovedPoints().get(i).point.getZ())
                                            + (waterSurface.getMovedPoints().get(i + waterSurface.getSize()).point.getZ() - waterSurface.getMovedPoints().get(i).point.getZ())) / 2)));
                        }
                    }
                    //right top corner
                    if (i == waterSurface.getSize() - 1) {
                        if (renderer.getEdgesLock()) {
                            points.add(new WaterArray(new Point3D(waterSurface.getMovedPoints().get(i).point.getX(), waterSurface.getMovedPoints().get(i).point.getY(), waterSurface.getMovedPoints().get(i).point.getZ() * renderer.getEdgesDamping()), 0.0));
                        } else {
                            points.add(new WaterArray(new Point3D(waterSurface.getMovedPoints().get(i).point.getX(), waterSurface.getMovedPoints().get(i).point.getY(), waterSurface.getMovedPoints().get(i).point.getZ()),
                                    (waterSurface.getMovedPoints().get(i).value * renderer.getWaterDamping() + ((waterSurface.getMovedPoints().get(i + waterSurface.getSize()).point.getZ() - waterSurface.getMovedPoints().get(i).point.getZ())
                                            + (waterSurface.getMovedPoints().get(i - 1).point.getZ() - waterSurface.getMovedPoints().get(i).point.getZ())) / 2)));
                        }
                    }
                    //left bottom corner
                    if (i == waterSurface.getMovedPoints().size() - waterSurface.getSize()) {
                        if (renderer.getEdgesLock()) {
                            points.add(new WaterArray(new Point3D(waterSurface.getMovedPoints().get(i).point.getX(), waterSurface.getMovedPoints().get(i).point.getY(), waterSurface.getMovedPoints().get(i).point.getZ() * renderer.getEdgesDamping()), 0.0));
                        } else {
                            points.add(new WaterArray(new Point3D(waterSurface.getMovedPoints().get(i).point.getX(), waterSurface.getMovedPoints().get(i).point.getY(), waterSurface.getMovedPoints().get(i).point.getZ()),
                                    (waterSurface.getMovedPoints().get(i).value * renderer.getWaterDamping() + ((waterSurface.getMovedPoints().get(i + 1).point.getZ() - waterSurface.getMovedPoints().get(i).point.getZ())
                                            + (waterSurface.getMovedPoints().get(i - waterSurface.getSize()).point.getZ() - waterSurface.getMovedPoints().get(i).point.getZ())) / 2)));
                        }
                    }
                    //right bottom corner
                    if (i == waterSurface.getMovedPoints().size() - 1) {
                        if (renderer.getEdgesLock()) {
                            points.add(new WaterArray(new Point3D(waterSurface.getMovedPoints().get(i).point.getX(), waterSurface.getMovedPoints().get(i).point.getY(), waterSurface.getMovedPoints().get(i).point.getZ() * renderer.getEdgesDamping()), 0.0));
                        } else {
                            points.add(new WaterArray(new Point3D(waterSurface.getMovedPoints().get(i).point.getX(), waterSurface.getMovedPoints().get(i).point.getY(), waterSurface.getMovedPoints().get(i).point.getZ()),
                                    (waterSurface.getMovedPoints().get(i).value * renderer.getWaterDamping() + ((waterSurface.getMovedPoints().get(i - 1).point.getZ() - waterSurface.getMovedPoints().get(i).point.getZ())
                                            + (waterSurface.getMovedPoints().get(i - waterSurface.getSize()).point.getZ() - waterSurface.getMovedPoints().get(i).point.getZ())) / 2)));
                        }
                    }
                } else {
                    //top edge
                    if (i < waterSurface.getSize()) {
                        if (renderer.getEdgesLock()) {
                            points.add(new WaterArray(new Point3D(waterSurface.getMovedPoints().get(i).point.getX(), waterSurface.getMovedPoints().get(i).point.getY(), waterSurface.getMovedPoints().get(i).point.getZ() * renderer.getEdgesDamping()), 0.0));
                        } else {
                            points.add(new WaterArray(new Point3D(waterSurface.getMovedPoints().get(i).point.getX(), waterSurface.getMovedPoints().get(i).point.getY(), waterSurface.getMovedPoints().get(i).point.getZ()),
                                    (waterSurface.getMovedPoints().get(i).value * renderer.getWaterDamping() + ((waterSurface.getMovedPoints().get(i + 1).point.getZ() - waterSurface.getMovedPoints().get(i).point.getZ())
                                            + (waterSurface.getMovedPoints().get(i + waterSurface.getSize()).point.getZ() - waterSurface.getMovedPoints().get(i).point.getZ())
                                            + (waterSurface.getMovedPoints().get(i - 1).point.getZ() - waterSurface.getMovedPoints().get(i).point.getZ())) / 3)));
                        }
                    }
                    //left edge
                    if (i % waterSurface.getSize() == 0) {
                        if (renderer.getEdgesLock()) {
                            points.add(new WaterArray(new Point3D(waterSurface.getMovedPoints().get(i).point.getX(), waterSurface.getMovedPoints().get(i).point.getY(), waterSurface.getMovedPoints().get(i).point.getZ() * renderer.getEdgesDamping()), 0.0));
                        } else {
                            points.add(new WaterArray(new Point3D(waterSurface.getMovedPoints().get(i).point.getX(), waterSurface.getMovedPoints().get(i).point.getY(), waterSurface.getMovedPoints().get(i).point.getZ()),
                                    (waterSurface.getMovedPoints().get(i).value * renderer.getWaterDamping() + ((waterSurface.getMovedPoints().get(i + 1).point.getZ() - waterSurface.getMovedPoints().get(i).point.getZ())
                                            + (waterSurface.getMovedPoints().get(i + waterSurface.getSize()).point.getZ() - waterSurface.getMovedPoints().get(i).point.getZ())
                                            + (waterSurface.getMovedPoints().get(i - waterSurface.getSize()).point.getZ() - waterSurface.getMovedPoints().get(i).point.getZ())) / 3)));
                        }
                    }
                    //right edge
                    if ((i + 1) % waterSurface.getSize() == 0) {
                        if (renderer.getEdgesLock()) {
                            points.add(new WaterArray(new Point3D(waterSurface.getMovedPoints().get(i).point.getX(), waterSurface.getMovedPoints().get(i).point.getY(), waterSurface.getMovedPoints().get(i).point.getZ() * renderer.getEdgesDamping()), 0.0));
                        } else {
                            points.add(new WaterArray(new Point3D(waterSurface.getMovedPoints().get(i).point.getX(), waterSurface.getMovedPoints().get(i).point.getY(), waterSurface.getMovedPoints().get(i).point.getZ()),
                                    (waterSurface.getMovedPoints().get(i).value * renderer.getWaterDamping() + ((waterSurface.getMovedPoints().get(i - 1).point.getZ() - waterSurface.getMovedPoints().get(i).point.getZ())
                                            + (waterSurface.getMovedPoints().get(i + waterSurface.getSize()).point.getZ() - waterSurface.getMovedPoints().get(i).point.getZ())
                                            + (waterSurface.getMovedPoints().get(i - waterSurface.getSize()).point.getZ() - waterSurface.getMovedPoints().get(i).point.getZ())) / 3)));
                        }
                    }
                    //bottom edge
                    if (i > waterSurface.getMovedPoints().size() - waterSurface.getSize()) {
                        if (renderer.getEdgesLock()) {
                            points.add(new WaterArray(new Point3D(waterSurface.getMovedPoints().get(i).point.getX(), waterSurface.getMovedPoints().get(i).point.getY(), waterSurface.getMovedPoints().get(i).point.getZ() * renderer.getEdgesDamping()), 0.0));
                        } else {
                            points.add(new WaterArray(new Point3D(waterSurface.getMovedPoints().get(i).point.getX(), waterSurface.getMovedPoints().get(i).point.getY(), waterSurface.getMovedPoints().get(i).point.getZ()),
                                    (waterSurface.getMovedPoints().get(i).value * renderer.getWaterDamping() + ((waterSurface.getMovedPoints().get(i + 1).point.getZ() - waterSurface.getMovedPoints().get(i).point.getZ())
                                            + (waterSurface.getMovedPoints().get(i - 1).point.getZ() - waterSurface.getMovedPoints().get(i).point.getZ())
                                            + (waterSurface.getMovedPoints().get(i - waterSurface.getSize()).point.getZ() - waterSurface.getMovedPoints().get(i).point.getZ())) / 3)));
                        }
                    }
                }
            } else {
                //internal points
                points.add(new WaterArray(new Point3D(waterSurface.getMovedPoints().get(i).point.getX(), waterSurface.getMovedPoints().get(i).point.getY(), waterSurface.getMovedPoints().get(i).point.getZ()),
                        (waterSurface.getMovedPoints().get(i).value * renderer.getWaterDamping() + ((waterSurface.getMovedPoints().get(i + 1).point.getZ() - waterSurface.getMovedPoints().get(i).point.getZ())
                                + (waterSurface.getMovedPoints().get(i + waterSurface.getSize()).point.getZ() - waterSurface.getMovedPoints().get(i).point.getZ())
                                + (waterSurface.getMovedPoints().get(i - 1).point.getZ() - waterSurface.getMovedPoints().get(i).point.getZ())
                                + (waterSurface.getMovedPoints().get(i - waterSurface.getSize()).point.getZ() - waterSurface.getMovedPoints().get(i).point.getZ())) / 4)));
            }
        }
        this.waterSurface = new WaterSurface(points, waterSurface.getSize(), waterSurface.getDensity());
        //lot of testing has been done
        //so far so good
    }

    public void waterCurve(double x, double y, double power, double size, int functionType) {
        List<WaterArray> points = new ArrayList<>();

        for (int i = 0; i < waterSurface.getStaticPoints().size(); i++) {
            if (i < waterSurface.getSize() || i % waterSurface.getSize() == 0 || (i + 1) % waterSurface.getSize() == 0 || i > waterSurface.getStaticPoints().size() - waterSurface.getSize()) {
                points.add(new WaterArray(new Point3D(waterSurface.getStaticPoints().get(i).point.getX(), waterSurface.getStaticPoints().get(i).point.getY(), waterSurface.getStaticPoints().get(i).point.getZ()), waterSurface.getStaticPoints().get(i).value));
            } else {
                points.add(new WaterArray(new Point3D(waterSurface.getStaticPoints().get(i).point.getX(), waterSurface.getStaticPoints().get(i).point.getY(),
                        power * functionCalculation(x, y, size, waterSurface.getStaticPoints().get(i).point, functionType) + waterSurface.getStaticPoints().get(i).point.getZ()), waterSurface.getStaticPoints().get(i).value));
            }
        }
        this.waterSurface = new WaterSurface(points, waterSurface.getSize(), waterSurface.getDensity());
    }

    private double functionCalculation(double x, double y, double size, Point3D point, int functionType) { //google ripple effect function - 2011
        double a = point.getX();
        double b = point.getY();
        double calculation;

        if (size == 0) { //if size is zero then function error - delete whole grid
            if (x == a && y == b) {
                return 1.0;
            } else {
                return 0.0;
            }
        }

        calculation = Math.sqrt(((a - x) * (a - x)) + ((b - y) * (b - y)));

        if (calculation <= size) {
            calculation = calculation / size;
            calculation = Math.PI * calculation;
            switch (functionType) {
                case 0:
                    calculation = (Math.cos(calculation) + 1) / 2; //cos function
                    break;
                case 1:
                    calculation = (Math.sin(calculation) + 1) / 2; //sombrero function
                    break;
            }
            return calculation;
        } else {
            return 0.0;
        }
    }

    public WaterSurface getWaterSurface() {
        return waterSurface;
    }

}