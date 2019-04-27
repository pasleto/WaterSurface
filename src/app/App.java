package app;

import com.jogamp.opengl.GLCapabilities;
import com.jogamp.opengl.GLProfile;
import com.jogamp.opengl.awt.GLCanvas;
import com.jogamp.opengl.util.FPSAnimator;
import renderer.Renderer;

import javax.swing.*;
import java.awt.*;
import java.awt.event.WindowAdapter;
import java.awt.event.WindowEvent;
import java.net.URL;

public class App {
    private static final int FPS = 60; // animator's target frames per second
    private Dimension dimension = new Dimension(1415, 800); //default dimension of window
    private Dimension minDimension = new Dimension(1415, 800); //minimal dimension of windows
    private URL iconURL = getClass().getResource("../assets/icon.png"); //app image url
    private ImageIcon icon = new ImageIcon(iconURL); //app image declaration

    private void start() {
        try {
            Frame frame = new Frame();
            frame.setTitle("UHK FIM - PGRF2 - Tomáš Pásler - Water Surface Simulation Project");
            frame.setSize(dimension);
            frame.setMinimumSize(minDimension);
            frame.setExtendedState(Frame.MAXIMIZED_BOTH);

            frame.setIconImage(icon.getImage()); //set app image

            GLProfile profile = GLProfile.get(GLProfile.GL2);
            GLCapabilities capabilities = new GLCapabilities(profile);
            capabilities.setRedBits(8);
            capabilities.setBlueBits(8);
            capabilities.setGreenBits(8);
            capabilities.setAlphaBits(8);
            capabilities.setDepthBits(24);

            GLCanvas canvas = new GLCanvas(capabilities);
            Renderer renderer = new Renderer();
            canvas.addGLEventListener(renderer);
            canvas.addMouseListener(renderer);
            canvas.addMouseMotionListener(renderer);
            canvas.addKeyListener(renderer);
            canvas.setSize(dimension);

            frame.add(canvas);

            final FPSAnimator animator = new FPSAnimator(canvas, FPS, true);

            frame.addWindowListener(new WindowAdapter() {
                @Override
                public void windowClosing(WindowEvent e) {
                    new Thread(() -> {
                        if (animator.isStarted()) animator.stop();
                        System.exit(0);
                    }).start();
                }
            });
            frame.pack();
            frame.setLocationRelativeTo(null);
            frame.setVisible(true);
            animator.start(); // start the animation loop

        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    public static void main(String[] args) {
        SwingUtilities.invokeLater(() -> new App().start());
    }

}