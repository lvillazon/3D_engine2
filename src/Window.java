import org.w3c.dom.ls.LSOutput;

import javax.swing.*;
import java.awt.*;
import java.awt.event.ComponentAdapter;
import java.awt.event.ComponentEvent;
import java.awt.image.BufferedImage;
import java.util.Arrays;

public class Window extends JFrame{
    // container class used to hold any of the 3D experiments
    // any panel class can be passed to the constructor to be displayed
    public static final int DEFAULT = 0; // flags to set whether to display optimised for single or dual monitor
    public static final int FULL_SCREEN = 1;
    public static final int LAPTOP = 2;
    private final Container pane;
    private int frameCount;          // used for fps calculation
    private long frameStart;         // used for fps calculation
    private long totalFrameDrawTime; // used for fps calculation

    public Window(int monitorConfig, JPanel contents) {
        setTitle("Raycaster");
        setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        pane = getContentPane();
        pane.setLayout(new BorderLayout());
        frameCount = 0;
        frameStart = 0;
        totalFrameDrawTime = 0;

        // panel for render output
        pane.add(contents, BorderLayout.CENTER);
        if (monitorConfig == LAPTOP) {
            setSize(1600, 900);  // native resolution on my laptop
            setLocation(-8, 0);       // normal position - top left of screen
        } else if (monitorConfig == FULL_SCREEN) {
            // put the window full-screen on the primary monitor (the laptop screen, for a laptop + monitor setup)
            setUndecorated(true);
            GraphicsDevice screen = GraphicsEnvironment.getLocalGraphicsEnvironment().getScreenDevices()[0];
            screen.setFullScreenWindow(this);
        } else {
            setSize(800, 600);
            setLocation(0, 0);       // normal position - top left of screen
        }

        setVisible(true);
    }

}

