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

    public void render(Graphics2D g2, double originX, double originY, Matrix3 transform) {
        g2.translate(originX, originY);
        g2.setColor(Color.WHITE);

        for (Triangle t: sides) {
            Vertex v1 = transform.transform(t.v1);
            Vertex v2 = transform.transform(t.v2);
            Vertex v3 = transform.transform(t.v3);
            Path2D path = new Path2D.Double();
            path.moveTo(v1.x, v1.y);
            path.lineTo(v2.x, v2.y);
            path.lineTo(v3.x, v3.y);
            path.closePath();
            g2.draw(path);
            int[] x = {(int)v1.x, (int)v2.x, (int)v3.x};
            int[] y = {(int)v1.y, (int)v2.y, (int)v3.y};
            Polygon fillTriangle = new Polygon(x, y, 3);
            g2.setColor(t.color);
            g2.fillPolygon(fillTriangle);
        }
    }
}
