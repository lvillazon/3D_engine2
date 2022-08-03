import javax.swing.*;
import java.awt.*;
import java.awt.image.BufferedImage;
import java.util.Arrays;

public class Window {
    // container class used to hold any of the 3D experiments
    // any panel class can be passed to the constructor to be displayed
    public static final int DEFAULT = 0; // flags to set whether to display optimised for single or dual monitor
    public static final int FULL_SCREEN = 1;
    public static final int LAPTOP = 2;
    private final JFrame frame;
    private final Container pane;
    private int frameCount;          // used for fps calculation
    private long frameStart;         // used for fps calculation
    private long totalFrameDrawTime; // used for fps calculation

    public Window(int monitorConfig, JPanel contents) {
        frame = new JFrame("Raycaster");
        frame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        pane = frame.getContentPane();
        pane.setLayout(new BorderLayout());
        frameCount = 0;
        frameStart = 0;
        totalFrameDrawTime = 0;

        // panel for render output
        pane.add(contents, BorderLayout.CENTER);
        if (monitorConfig == LAPTOP) {
            frame.setSize(1600, 900);  // native resolution on my laptop
            frame.setLocation(-8, 0);       // normal position - top left of screen
        } else if (monitorConfig == FULL_SCREEN) {
            // put the window full-screen on the primary monitor (the laptop screen, for a laptop + monitor setup)
            frame.setUndecorated(true);
            GraphicsDevice screen = GraphicsEnvironment.getLocalGraphicsEnvironment().getScreenDevices()[0];
            screen.setFullScreenWindow(frame);
        } else {
            frame.setSize(800, 600);
            frame.setLocation(0, 0);       // normal position - top left of screen
        }
        frame.setVisible(true);
    }

}

