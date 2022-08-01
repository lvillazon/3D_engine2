import javax.swing.*;
import java.awt.*;
import java.awt.event.KeyEvent;

public class RaycastView extends JPanel {
    // shows the 3D view in the maze, from the player POV
    private TopDownMapView map;

    public RaycastView(TopDownMapView map) {
        this.map = map;
    }

    @Override
    public void paint(Graphics g) {
        super.paint(g);
        Graphics2D g2 = (Graphics2D) g;
        // draw the current view in the maze, using raycasting

    }
}