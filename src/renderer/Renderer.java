package renderer;

import com.jogamp.opengl.GL2;
import com.jogamp.opengl.GLAutoDrawable;
import com.jogamp.opengl.GLEventListener;
import com.jogamp.opengl.glu.GLU;

import java.awt.event.MouseListener;
import java.awt.event.MouseMotionListener;
import java.awt.event.MouseEvent;
import java.awt.event.KeyListener;
import java.awt.event.KeyEvent;
import java.util.Random;

import utils.Point2D;
import water.WaterMoving;
import utils.TextController;

import javax.swing.*;

public class Renderer implements GLEventListener, MouseListener, MouseMotionListener, KeyListener {
    private GLU glu;
    private int width, height;
    private double dx, dy;
    private boolean perspective = true; //default perspective
    private boolean textPrint = true;
    private int waterSize = 50; //size of water grid
    private int density = 1; //density points, grid refining
    private int zoom = 55; //default zoom set
    private boolean edgesLock = true; //default edges lock
    private boolean edgesLockVisible = true;
    private boolean waterSurfaceFill = false; //default wireframe mode
    private Point2D centerPoint = new Point2D(-0.5, -0.5);
    private long oldMils = System.currentTimeMillis();
    private float fps;
    private double waterDamping = 0.99; //damping coefficient
    private double edgesDamping = 0.95;
    private double mousePower = -0.25; //mouse drag function power
    private double mouseSize = 2; //mouse drag function size
    private double functionPower = -1; //mouse press function power
    private double functionSize = 4; //mouse press function size
    private boolean mouseDraggedBoolean = false;
    private boolean mousePressedBoolean = false;
    private boolean loopState = false;
    private boolean rain = false;
    private boolean mapValue = true;
    private WaterMoving waterMoving = new WaterMoving(waterSize, density, this);


    @Override
    public void init(GLAutoDrawable glAutoDrawable) {
        GL2 gl = glAutoDrawable.getGL().getGL2();
        glu = new GLU();

        TextController.printParams(gl);
        materialSettings(glAutoDrawable);

        gl.glEnable(GL2.GL_DEPTH_TEST);
        gl.glFrontFace(GL2.GL_CCW);
        gl.glPolygonMode(GL2.GL_FRONT, GL2.GL_FILL);
        gl.glPolygonMode(GL2.GL_BACK, GL2.GL_LINE);
        gl.glDisable(GL2.GL_CULL_FACE); //
        gl.glDisable(GL2.GL_TEXTURE_2D);
        gl.glDisable(GL2.GL_LIGHTING);
        gl.glMatrixMode(GL2.GL_MODELVIEW);
        gl.glLoadIdentity();
    }

    @Override
    public void display(GLAutoDrawable glAutoDrawable) {
        GL2 gl = glAutoDrawable.getGL().getGL2();

        fpsRender();

        gl.glClearColor(0f, 0f, 0f, 1f);
        gl.glClear(GL2.GL_COLOR_BUFFER_BIT | GL2.GL_DEPTH_BUFFER_BIT);
        gl.glMatrixMode(GL2.GL_MODELVIEW);
        gl.glLoadIdentity();

        gl.glPushMatrix();
        gl.glLoadIdentity();

        lightRender(glAutoDrawable);

        gl.glMatrixMode(GL2.GL_PROJECTION);
        gl.glLoadIdentity();

        cameraRender(glAutoDrawable);
        edgesRender(glAutoDrawable);
        gridRender(glAutoDrawable);
        textRender(glAutoDrawable);
    }

    @Override
    public void reshape(GLAutoDrawable glAutoDrawable, int x, int y, int width, int height) {
        this.width = width;
        this.height = height;
    }

    @Override
    public void keyPressed(KeyEvent e) {
        switch (e.getKeyCode()) {
            case KeyEvent.VK_ESCAPE: //close app
                System.exit(1);
                break;
            case KeyEvent.VK_R: //scene resetScene
                stateChange("resetScene");
                break;
            case KeyEvent.VK_X: //mode switch wireframe X full
                stateChange("waterFill");
                break;
            case KeyEvent.VK_E: //edges lock
                stateChange("edgesLock");
                break;
            case KeyEvent.VK_Q: //edges lock visible
                stateChange("edgesLockVisible");
                break;
            case KeyEvent.VK_C: //perspective x orthogonal
                stateChange("perspective");
                break;
            case KeyEvent.VK_T: //textPrint
                stateChange("textPrint");
                break;
            case KeyEvent.VK_F: //mapValue
                stateChange("mapValue");
                break;
            case KeyEvent.VK_UP: //waterSize++
                stateChange("waterSizeUp");
                break;
            case KeyEvent.VK_DOWN: //waterSize--
                stateChange("waterSizeDown");
                break;
            case KeyEvent.VK_W: //zoom--
                stateChange("zoomDown");
                break;
            case KeyEvent.VK_S: //zoom++
                stateChange("zoomUp");
                break;
            case KeyEvent.VK_RIGHT: //density++
                stateChange("densityPointsUp");
                break;
            case KeyEvent.VK_LEFT: //density--
                stateChange("densityPointsDown");
                break;
            case KeyEvent.VK_1: //rainFunction
            case KeyEvent.VK_NUMPAD1:
                stateChange("rain");
                rainFunction();
                break;
            case KeyEvent.VK_2: //CosLoop
            case KeyEvent.VK_NUMPAD2:
                stateChange("loop");
                loopFunction(0);
                break;
            case KeyEvent.VK_3: //SinLoop
            case KeyEvent.VK_NUMPAD3:
                stateChange("loop");
                loopFunction(1);
                break;
            case KeyEvent.VK_4: //CosFunction example
            case KeyEvent.VK_NUMPAD4:
                waterMoving.waterCurve(centerPoint.getX(), centerPoint.getY(), -8, 8, 0);
                break;
            case KeyEvent.VK_5: //SinFunction example
            case KeyEvent.VK_NUMPAD5:
                waterMoving.waterCurve(centerPoint.getX(), centerPoint.getY(), -2, 8, 1);
                break;
        }
    }

    @Override
    public void mousePressed(MouseEvent e) {
        dx = e.getX();
        dy = e.getY();
        if (SwingUtilities.isRightMouseButton(e)) {
            waterMoving.waterCurve(centerPoint.getX() + calculateCoordinates(dx, dy).getX(), centerPoint.getY() + calculateCoordinates(dx, dy).getY(), functionPower, functionSize, 1);
            mousePressedBoolean = true;
        }
    }

    @Override
    public void mouseDragged(MouseEvent e) {
        dx = e.getX();
        dy = e.getY();
        if (SwingUtilities.isLeftMouseButton(e)) {
            waterMoving.waterCurve(centerPoint.getX() + calculateCoordinates(dx, dy).getX(), centerPoint.getY() + calculateCoordinates(dx, dy).getY(), mousePower, mouseSize, 0);
            mouseDraggedBoolean = true;
        }
    }

    @Override
    public void mouseReleased(MouseEvent e) {
        mouseDraggedBoolean = false;
        mousePressedBoolean = false;
    }

    private Point2D calculateCoordinates(double dx, double dy) { //mapped drawing to water surface on window size
        int valueMap;
        if (mapValue) {
            valueMap = 1;
        } else {
            valueMap = 2;
        }
        int halfOfWaterSize = (waterMoving.getWaterSurface().getSize() / waterMoving.getWaterSurface().getDensity()) / valueMap;

        //need to be invert because of openGl and window coordinate system
        double newY = ((2 * dx) / (width - 1) - 1) * halfOfWaterSize;
        double newX = ((2 * dy) / (height - 1) - 1) * halfOfWaterSize;

        return new Point2D(newX, newY);
    }

    private void gridRender(GLAutoDrawable glAutoDrawable) {
        GL2 gl = glAutoDrawable.getGL().getGL2();
        gl.glBegin(GL2.GL_TRIANGLES); //draw of water surface
        gl.glColor3d(0.3, 0.6, 0.9); //wireframe color
        waterMoving.drawSurfaceLevel(glAutoDrawable);
        gl.glEnd();
    }

    private void edgesRender(GLAutoDrawable glAutoDrawable) { //edges border when they are locked
        GL2 gl = glAutoDrawable.getGL().getGL2();
        if (edgesLock && edgesLockVisible) {
            gl.glBegin(GL2.GL_LINE_LOOP);
            gl.glColor3d(0.3, 0.6, 0.9); //wireframe color
            //index 0 -> top left corner
            gl.glVertex3d(waterMoving.getWaterSurface().getStaticPoints().get(0).point.getX() - 0.5,
                    waterMoving.getWaterSurface().getStaticPoints().get(0).point.getY() - 0.5,
                    waterMoving.getWaterSurface().getStaticPoints().get(0).point.getZ());
            //index waterSurface.getSize()-1 -> top right corner
            gl.glVertex3d(waterMoving.getWaterSurface().getStaticPoints().get(waterMoving.getWaterSurface().getSize() - 1).point.getX() - 0.5,
                    waterMoving.getWaterSurface().getStaticPoints().get(waterMoving.getWaterSurface().getSize() - 1).point.getY() + 0.5,
                    waterMoving.getWaterSurface().getStaticPoints().get(waterMoving.getWaterSurface().getSize() - 1).point.getZ());
            //index waterSurface.getStaticPoints().size()-1 -> bottom right corner
            gl.glVertex3d(waterMoving.getWaterSurface().getStaticPoints().get(waterMoving.getWaterSurface().getStaticPoints().size() - 1).point.getX() + 0.5,
                    waterMoving.getWaterSurface().getStaticPoints().get(waterMoving.getWaterSurface().getStaticPoints().size() - 1).point.getY() + 0.5,
                    waterMoving.getWaterSurface().getStaticPoints().get(waterMoving.getWaterSurface().getStaticPoints().size() - 1).point.getZ());
            //index waterSurface.getStaticPoints().size()-waterSurface.getSize() -> bottom left corner
            gl.glVertex3d(waterMoving.getWaterSurface().getStaticPoints().get(waterMoving.getWaterSurface().getStaticPoints().size() - waterMoving.getWaterSurface().getSize()).point.getX() + 0.5,
                    waterMoving.getWaterSurface().getStaticPoints().get(waterMoving.getWaterSurface().getStaticPoints().size() - waterMoving.getWaterSurface().getSize()).point.getY() - 0.5,
                    waterMoving.getWaterSurface().getStaticPoints().get(waterMoving.getWaterSurface().getStaticPoints().size() - waterMoving.getWaterSurface().getSize()).point.getZ());
            gl.glEnd();
        }
    }

    private void lightRender(GLAutoDrawable glAutoDrawable) {
        GL2 gl = glAutoDrawable.getGL().getGL2();
        if (getWaterSurfaceFill()) { //set lighting if grid is set to filled one
            float[] light_position;
            light_position = new float[]{50, 50, 50, 0.0f};// light point
            gl.glLightfv(GL2.GL_LIGHT0, GL2.GL_POSITION, light_position, 0);
            gl.glEnable(GL2.GL_LIGHTING);
            gl.glEnable(GL2.GL_LIGHT0);
            gl.glEnable(GL2.GL_CULL_FACE);
            gl.glCullFace(GL2.GL_BACK);
            gl.glShadeModel(GL2.GL_SMOOTH);
            gl.glFrontFace(GL2.GL_CCW);
        } else {
            gl.glDisable(GL2.GL_LIGHTING);
            gl.glDisable(GL2.GL_LIGHT0);
            gl.glDisable(GL2.GL_CULL_FACE);
            gl.glShadeModel(GL2.GL_LINES);
            gl.glFrontFace(GL2.GL_CCW);
        }
    }

    private void cameraRender(GLAutoDrawable glAutoDrawable) {
        GL2 gl = glAutoDrawable.getGL().getGL2();
        if (perspective) {
            glu.gluPerspective(zoom - 10, width / (float) height, 0.1f, 150.0f); //camera perspective
            glu.gluLookAt(zoom, centerPoint.getY(), 20, centerPoint.getX(), centerPoint.getY(), 0, 0, 0, 1); // view
        } else {
            gl.glOrtho(-20 * width / (float) height, 20 * width / (float) height, -20, 20, 0.1f, 150.0f); //camera orthogonal
            glu.gluLookAt(25, centerPoint.getY(), 20, centerPoint.getX(), centerPoint.getY(), 0, 0, 0, 1); // view
        }
    }

    private void textRender(GLAutoDrawable glAutoDrawable) {
        if (textPrint) {
            TextController.printText(glAutoDrawable, this); //text rendering
        }
    }

    private void materialSettings(GLAutoDrawable glAutoDrawable) {
        GL2 gl = glAutoDrawable.getGL().getGL2();
        // material settings
        float[] mat_dif = new float[]{0.3f, 0.6f, 0.9f, 1.0f};
        float[] mat_spec = new float[]{0.3f, 0.6f, 0.9f, 1.0f};
        float[] mat_amb = new float[]{0.05f, 0.1f, 0.15f, 1.0f};
        gl.glMaterialfv(GL2.GL_FRONT, GL2.GL_AMBIENT, mat_amb, 0);
        gl.glMaterialfv(GL2.GL_FRONT, GL2.GL_DIFFUSE, mat_dif, 0);
        gl.glMaterialfv(GL2.GL_FRONT, GL2.GL_SPECULAR, mat_spec, 0);
        // light settings
        float[] light_amb = new float[]{0.05f, 0.1f, 0.15f, 1.0f};
        float[] light_dif = new float[]{0.3f, 0.6f, 0.9f, 1.0f};
        float[] light_spec = new float[]{0.24f, 0.48f, 0.48f, 1.0f};
        gl.glLightfv(GL2.GL_LIGHT0, GL2.GL_AMBIENT, light_amb, 0);
        gl.glLightfv(GL2.GL_LIGHT0, GL2.GL_DIFFUSE, light_dif, 0);
        gl.glLightfv(GL2.GL_LIGHT0, GL2.GL_SPECULAR, light_spec, 0);
    }

    private void fpsRender() {
        long mils = System.currentTimeMillis();
        fps = 1000 / (float) (mils - oldMils);
        oldMils = mils;
    }

    private void stateChange(String state) {
        switch (state) {
            case "perspective":
                perspective = !perspective;
                break;
            case "textPrint":
                textPrint = !textPrint;
                break;
            case "edgesLock":
                edgesLock = !edgesLock;
                if (edgesLock) {
                    edgesLockVisible = true;
                }
                if (!edgesLock) {
                    edgesLockVisible = false;
                }
                break;
            case "edgesLockVisible":
                if (edgesLock) {
                    edgesLockVisible = !edgesLockVisible;
                }
                break;
            case "waterFill":
                waterSurfaceFill = !waterSurfaceFill;
                break;
            case "loop":
                loopState = !loopState;
                break;
            case "rain":
                rain = !rain;
                break;
            case "mapValue":
                mapValue = !mapValue;
                break;
            case "waterSizeUp":
                if (waterSize < 100) {
                    waterSize = waterSize + 2;
                    waterMoving = new WaterMoving(waterSize, density, this);
                }
                break;
            case "waterSizeDown":
                if (waterSize > 10) {
                    waterSize = waterSize - 2;
                    waterMoving = new WaterMoving(waterSize, density, this);
                }
                break;
            case "zoomUp":
                if (zoom < 85)
                    zoom++;
                break;
            case "zoomDown":
                if (zoom > 25)
                    zoom--;
                break;
            case "densityPointsUp":
                if (density < 8) {
                    density = density * 2;
                    waterMoving = new WaterMoving(waterSize, density, this);
                }
                break;
            case "densityPointsDown":
                if (density > 1) {
                    density = density / 2;
                    waterMoving = new WaterMoving(waterSize, density, this);
                }
                break;
            case "resetScene":
                density = 1;
                waterSize = 50;
                zoom = 55;
                perspective = true;
                textPrint = true;
                edgesLock = true;
                edgesLockVisible = true;
                waterSurfaceFill = false;
                waterDamping = 0.99;
                edgesDamping = 0.95;
                mousePower = -0.25;
                mouseSize = 2;
                functionPower = -1;
                functionSize = 4;
                loopState = false;
                rain = false;
                mapValue = true;
                waterMoving.resetScene();
                this.waterMoving = new WaterMoving(waterSize, density, this);
                break;
        }
    }

    private void loopFunction(int type) {
        switch (type) {
            case 0:
                if (loopState) {
                    waterDamping = 1.0;
                    waterMoving.waterCurve(centerPoint.getX(), centerPoint.getY(), -5, 5, 0);
                } else {
                    waterDamping = 0.99;
                }
                break;
            case 1:
                if (loopState) {
                    waterDamping = 1.0;
                    waterMoving.waterCurve(centerPoint.getX(), centerPoint.getY(), -1, 4, 1);
                } else {
                    waterDamping = 0.99;
                }
                break;
        }
    }

    private void rainFunction() {
        int minDropCoordinates = -waterSize / 2;
        int maxDropCoordinates = waterSize / 2;
        int minDelayTime = 100;
        int maxDelayTime = 1000;
        int minDropPower = 1;
        int maxDropPower = 4;

        if (rain) {
            int randomX = new Random().nextInt((maxDropCoordinates - minDropCoordinates) + 1) + minDropCoordinates;
            int randomY = new Random().nextInt((maxDropCoordinates - minDropCoordinates) + 1) + minDropCoordinates;
            int randomPower = new Random().nextInt((maxDropPower - minDropPower) + 1) - minDropPower;
            int randomTime = new Random().nextInt((maxDelayTime - minDelayTime) + 1) + minDelayTime;

            waterMoving.waterCurve(randomX, randomY, randomPower, 0, 1);

            new java.util.Timer().schedule(
                    new java.util.TimerTask() {
                        @Override
                        public void run() {
                            rainFunction();
                        }
                    },
                    randomTime
            );
        }
    }

    public boolean getEdgesLock() {
        return edgesLock;
    }

    public boolean getEdgesLockVisible() {
        return edgesLockVisible;
    }

    public boolean getWaterSurfaceFill() {
        return waterSurfaceFill;
    }

    public WaterMoving getWaterMoving() {
        return waterMoving;
    }

    public int getWidth() {
        return width;
    }

    public int getHeight() {
        return height;
    }

    public boolean getPerspective() {
        return perspective;
    }

    public int getWaterSize() {
        return waterSize;
    }

    public int getDensity() {
        return density;
    }

    public int getZoom() {
        return zoom;
    }

    public float getFps() {
        return fps;
    }

    public double getWaterDamping() {
        return waterDamping;
    }

    public double getEdgesDamping() {
        return edgesDamping;
    }

    public double getMousePower() {
        return mousePower;
    }

    public double getMouseSize() {
        return mouseSize;
    }

    public double getFunctionPower() {
        return functionPower;
    }

    public double getFunctionSize() {
        return functionSize;
    }

    public boolean getMouseDragged() {
        return mouseDraggedBoolean;
    }

    public boolean getMousePressed() {
        return mousePressedBoolean;
    }

    public boolean getLoopState() {
        return loopState;
    }

    public boolean getRain() {
        return rain;
    }

    public boolean getMapValue() {
        return mapValue;
    }

    @Override
    public void keyReleased(KeyEvent e) { //not used
    }

    @Override
    public void mouseMoved(MouseEvent e) { //not used
    }

    @Override
    public void mouseEntered(MouseEvent e) { //not used
    }

    @Override
    public void mouseExited(MouseEvent e) { //not used
    }

    @Override
    public void keyTyped(KeyEvent e) { //not used
    }

    @Override
    public void dispose(GLAutoDrawable glAutoDrawable) { //not used
    }

    @Override
    public void mouseClicked(MouseEvent e) { //not used
    }

}