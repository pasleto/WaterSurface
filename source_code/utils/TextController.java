package utils;

import java.awt.Font;

import com.jogamp.opengl.GL2;
import com.jogamp.opengl.GLAutoDrawable;
import com.jogamp.opengl.util.awt.TextRenderer;
import renderer.Renderer;

public class TextController {

    public static void printParams(GL2 gl) { //printing parameters into console
        System.out.println("Init GL is " + gl.getClass().getName());
        System.out.println("GL_VENDOR " + gl.glGetString(GL2.GL_VENDOR));
        System.out.println("GL_RENDERER " + gl.glGetString(GL2.GL_RENDERER));
        System.out.println("GL_VERSION " + gl.glGetString(GL2.GL_VERSION));
        System.out.println("GL_EXTENSIONS " + gl.glGetString(GL2.GL_EXTENSIONS));
    }

    public static void printText(GLAutoDrawable glAutoDrawable, Renderer renderer) { //text generating on window frame
        GL2 gl = glAutoDrawable.getGL().getGL2();

        gl.glPopMatrix();
        gl.glColor3f(1f, 1f, 1f);

        String projectText = "Project:   Water Surface Simulation using OpenGL in Java";
        String divider = "__________________________________________________________";
        String glText = "GPU using OpenGL:  " + gl.glGetString(GL2.GL_RENDERER) + "  -  " + gl.glGetString(GL2.GL_VENDOR);
        String topText = "Controls:  ESC - Close  |  R - Reset  |  T - Hide / Show Text  |  F - Full / Half  Window Mouse  |  W / S - Camera Zoom + / -  |  [↑] / [↓] - GridSize + / -  |  [→] / [←] - Density + / -  "
                + ((renderer.getWaterSurfaceFill()) ? "|  X - WireFrame mode  " : "|  X - FillFrame mode  ")
                + ((renderer.getEdgesLock()) ? "|  E - Unlock Edges  " : "|  E - Lock Edges  ")
                + ((renderer.getEdgesLockVisible()) ? "|  Q - Hide Edges Lock  " : "|  Q - Show Edges Lock  ")
                + ((renderer.getPerspective()) ? "|  C - Orthogonal  " : "|  P - Perspective  ");
        String infoText = "Variable:   Zoom  -  " + renderer.getZoom() + "  |  Size  -  " + renderer.getWaterSize()
                + " ( " + renderer.getWaterMoving().getWaterSurface().getSize() + " ) " + "  |  Density  -  " + renderer.getDensity();
        String loopText = "Functions Controls:  1  -  Rain Loop  |  2  -  Cos Loop  |  3  -  Sin Loop  |  4  -  Cos Function Example  |  5  -  Sin Function Example  |   ---   To stop LOOP functions perform RESET or loop function again to slowly stop";
        String loopStateText = "Loop State:  " + ((renderer.getLoopState()) ? "On  |  Never ending waves" : "Off");
        String rainText = "Rain State:  " + ((renderer.getRain()) ? "On  |  Never ending rain effect" : "Off");
        String edgesText = "Edges:  " + ((renderer.getEdgesLock()) ? "Locked" : "Unlocked");
        String edgesLockText = "Edges Lock Visible:  " + ((renderer.getEdgesLockVisible()) ? "Yes" : "No");
        String frameText = "Draw:  " + ((renderer.getWaterSurfaceFill()) ? "FillFrame" : "WireFrame");
        String cameraText = "Camera:  " + ((renderer.getPerspective()) ? "Perspective" : "Orthogonal");
        String mouseText1 = "LMB  (drag)  -   power:   " + renderer.getMousePower() + " | size:   " + renderer.getMouseSize();
        String mouseText2 = "RMB (press)  -   power:   " + renderer.getFunctionPower() + " | size:   " + renderer.getFunctionSize();
        String fpsText = "FPS:  " + String.format("%.2f", renderer.getFps());
        String mapValueText = "Mouse Coordinates Mapped To:  " + ((renderer.getMapValue()) ? "Inner Half  Window Size" : "Full Window Size");
        String bottomText = "UHK FIM - PGRF2 - Tomáš Pásler - github.com/pasleto";

        TextController.drawStr2D(glAutoDrawable, 10, renderer.getHeight() - 20, projectText, false, true);
        TextController.drawStr2D(glAutoDrawable, 10, renderer.getHeight() - 27, divider, false, false);
        TextController.drawStr2D(glAutoDrawable, 10, renderer.getHeight() - 45, glText, false, false);
        TextController.drawStr2D(glAutoDrawable, 10, renderer.getHeight() - 65, topText, false, false);
        TextController.drawStr2D(glAutoDrawable, 10, renderer.getHeight() - 85, loopText, false, false);
        TextController.drawStr2D(glAutoDrawable, 10, renderer.getHeight() - 105, infoText, false, false);
        TextController.drawStr2D(glAutoDrawable, 10, renderer.getHeight() - 125, rainText, renderer.getRain(), false);
        TextController.drawStr2D(glAutoDrawable, 10, renderer.getHeight() - 145, loopStateText, renderer.getLoopState(), false);
        TextController.drawStr2D(glAutoDrawable, 10, renderer.getHeight() - 165, mouseText1, renderer.getMouseDragged(), false);
        TextController.drawStr2D(glAutoDrawable, 10, renderer.getHeight() - 185, mouseText2, renderer.getMousePressed(), false);
        TextController.drawStr2D(glAutoDrawable, 10, renderer.getHeight() - 205, edgesText, renderer.getEdgesLock(), false);
        TextController.drawStr2D(glAutoDrawable, 10, renderer.getHeight() - 225, edgesLockText, renderer.getEdgesLockVisible(), false);
        TextController.drawStr2D(glAutoDrawable, 10, renderer.getHeight() - 245, frameText, renderer.getWaterSurfaceFill(), false);
        TextController.drawStr2D(glAutoDrawable, 10, renderer.getHeight() - 265, cameraText, !renderer.getPerspective(), false);
        TextController.drawStr2D(glAutoDrawable, 10, renderer.getHeight() - 285, mapValueText, !renderer.getMapValue(), false);
        TextController.drawStr2D(glAutoDrawable, 10, renderer.getHeight() - 305, fpsText, false, false);
        TextController.drawStr2D(glAutoDrawable, renderer.getWidth() - 390, 10, bottomText, false, true);
    }

    private static void drawStr2D(GLAutoDrawable glAutoDrawable, int x, int y, String s, boolean red, boolean big) {
        if (glAutoDrawable == null)
            return;
        GL2 gl = glAutoDrawable.getGL().getGL2();
        //push all parameters
        int shaderProgram = pushAll(glAutoDrawable);
        gl.glDisable(GL2.GL_TEXTURE_2D);
        gl.glDisable(GL2.GL_LIGHTING);
        gl.glFrontFace(GL2.GL_CCW);
        gl.glPolygonMode(GL2.GL_FRONT, GL2.GL_FILL);
        gl.glPolygonMode(GL2.GL_BACK, GL2.GL_LINE);

        gl.glPolygonMode(GL2.GL_FRONT, GL2.GL_FILL);
        gl.glViewport(0, 0, glAutoDrawable.getSurfaceWidth(),
                glAutoDrawable.getSurfaceHeight());
        TextRenderer renderer;

        if (big) {
            renderer = new TextRenderer(new Font("SansSerif", Font.BOLD, 15));
        } else {
            renderer = new TextRenderer(new Font("SansSerif", Font.PLAIN, 12));
        }

        if (red) {
            renderer.setColor(1.0f, 0.0f, 0f, 1.0f); //red color
        } else {
            renderer.setColor(1.0f, 1.0f, 0f, 1.0f); //yellow color
        }
        renderer.beginRendering(glAutoDrawable.getSurfaceWidth(), glAutoDrawable.getSurfaceHeight());
        renderer.draw(s, x, y);
        renderer.endRendering();
        //pop all parameters
        popAll(glAutoDrawable, shaderProgram);
    }

    private static int pushAll(GLAutoDrawable glAutoDrawable) {
        if (glAutoDrawable == null)
            return 0;
        GL2 gl = glAutoDrawable.getGL().getGL2();
        //push all parameters
        int[] shaderProgram = new int[1];
        gl.glUseProgram(0);
        gl.glGetIntegerv(GL2.GL_CURRENT_PROGRAM, shaderProgram, 0);
        gl.glPushAttrib(GL2.GL_ENABLE_BIT);
        gl.glPushAttrib(GL2.GL_DEPTH_BUFFER_BIT);
        gl.glPushAttrib(GL2.GL_VIEWPORT_BIT);
        gl.glPushAttrib(GL2.GL_TEXTURE_BIT);
        gl.glPushAttrib(GL2.GL_POLYGON_BIT);
        gl.glDisable(GL2.GL_DEPTH_TEST);
        gl.glDisableVertexAttribArray(0);
        gl.glDisableClientState(GL2.GL_VERTEX_ARRAY);
        gl.glDisableClientState(GL2.GL_COLOR_ARRAY);
        gl.glDisableClientState(GL2.GL_TEXTURE_COORD_ARRAY);
        gl.glDepthMask(false);
        gl.glMatrixMode(GL2.GL_PROJECTION);
        gl.glPushMatrix();
        gl.glLoadIdentity();
        gl.glMatrixMode(GL2.GL_MODELVIEW);
        gl.glPushMatrix();
        gl.glLoadIdentity();

        return shaderProgram[0];
    }

    private static void popAll(GLAutoDrawable glAutoDrawable, int shaderProgram) {
        if (glAutoDrawable == null)
            return;
        GL2 gl = glAutoDrawable.getGL().getGL2();
        //pop all parameters
        gl.glPopMatrix();
        gl.glMatrixMode(GL2.GL_PROJECTION);
        gl.glPopMatrix();
        gl.glPopAttrib();
        gl.glPopAttrib();
        gl.glPopAttrib();
        gl.glPopAttrib();
        gl.glPopAttrib();
        gl.glUseProgram(shaderProgram);
    }

}