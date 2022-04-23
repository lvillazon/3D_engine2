import java.awt.*;
import java.awt.geom.Path2D;
import java.awt.image.BufferedImage;
import java.util.ArrayList;

public class Tetrahedron {
    ArrayList<Triangle> sides;

    public Tetrahedron(Vertex v1, Vertex v2, Vertex v3, Vertex v4) {
        sides = new ArrayList<>();
        sides.add(new Triangle(v1, v2, v3, Color.WHITE));
        sides.add(new Triangle(v1, v2, v4, Color.RED));
        sides.add(new Triangle(v3, v4, v1, Color.GREEN));
        sides.add(new Triangle(v3, v4, v2, Color.BLUE));
    }

    public ArrayList<Triangle> getTriangles() {
        return sides;
    }

}
