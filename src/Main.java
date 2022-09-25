import javax.swing.*;
import java.awt.*;

public class Main {

    public static void main(String[] args) {

        // test polyhedron rotation
        /*
        RotationViewer v = new RotationViewer();
        Window w = new Window(Window.FULL_SCREEN, v);
        Tetrahedron t = new Tetrahedron(new Vertex(100, 100, 100),
                new Vertex(-100, -100, 100),
                new Vertex(-100, 100, -100),
                new Vertex(100, -100, -100)
        );
        v.addShape(t);
        // TODO: replace with proper swing timer object
        while (true) {
           v.redraw();
        }

         */


        // test raycasting
        TopDownMapView map = new TopDownMapView();
        FirstPersonView threeD = new FirstPersonView(map);
        // create side-by-side split-screen panel for the map and raycast view
        JPanel splitScreen = new JPanel();
        splitScreen.setLayout(new BorderLayout());
        System.out.println("Adding views");
        splitScreen.add(map, BorderLayout.WEST);
        splitScreen.add(threeD, BorderLayout.EAST);

        Window w2 = new Window(Window.LAPTOP, splitScreen);
        System.out.println("Setting visible");
        w2.setVisible(true);
        System.out.println("Finished setup");

    }
}
