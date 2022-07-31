import javax.swing.*;
import java.awt.*;
import java.awt.image.BufferedImage;
import java.util.Arrays;

public class Window {
    // container class used to hold any of the 3D experiments
    // any panel class can be passed to the constructor to be displayed
    public static final int LAPTOP = 0; // flags to set whether to display optimised for laptop or desktop
    public static final int DESKTOP = 1;
    private final JFrame frame;
    private final Container pane;
    private int frameCount;          // used for fps calculation
    private long frameStart;         // used for fps calculation
    private long totalFrameDrawTime; // used for fps calculation

    public Window(int monitorConfig, JPanel contents) {
        frame = new JFrame();
        frame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        pane = frame.getContentPane();
        pane.setLayout(new BorderLayout());
        frameCount = 0;
        frameStart = 0;
        totalFrameDrawTime = 0;

        // panel for render output
        pane.add(contents, BorderLayout.CENTER);
        if (monitorConfig == LAPTOP) {
            frame.setSize(1616, 876);  // full screen on my laptop
            frame.setLocation(160, 1072);  // position for laptop when running dual monitors
        } else {
            frame.setSize(800, 600);
            frame.setLocation(0, 0);       // normal position - top left of screen
        }
        frame.setVisible(true);
    }

}

