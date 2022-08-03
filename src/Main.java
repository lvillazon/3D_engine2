import javax.swing.*;
import java.awt.*;

public class Main {

    public static void main(String[] args) {
        /*
        // test polyhedron rotation
        RotationViewer v = new RotationViewer();
        Window w = new Window(Window.DESKTOP, v);
        Tetrahedron t = new Tetrahedron(new Vertex(100, 100, 100),
                new Vertex(-100, -100, 100),
                new Vertex(-100, 100, -100),
                new Vertex(100, -100, -100)
        );
        v.addShape(t);
        // TODO: replace with proper swing timer object
        //while (true) {
        //    v.redraw();
        //}
        */

        // test raycasting
        TopDownMapView map = new TopDownMapView();
        FirstPersonView threeD = new FirstPersonView(map);
        // create side-by-side split-screen panel for the map and raycast view
        JPanel splitScreen = new JPanel();
        splitScreen.setLayout(null);
        Rectangle bounds = GraphicsEnvironment.getLocalGraphicsEnvironment().getMaximumWindowBounds();
        map.setBounds(0,0, bounds.width/2, bounds.height);
        splitScreen.add(map);
        threeD.setBounds(bounds.width/2,0, bounds.width/2, bounds.height);
        splitScreen.add(threeD);
        splitScreen.setVisible(true);

        Window w2 = new Window(Window.LAPTOP, splitScreen);
    }
}
