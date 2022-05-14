import java.awt.*;

public class Triangle {
    // fundamental polygon for rendering all other shapes
    Vertex v1;
    Vertex v2;
    Vertex v3;
    Color color;

    public Triangle() {
        // instantiate an empty triangle(so it can have vertices added later)
        v1 = new Vertex(0,0,0);
        v2 = new Vertex(0,0,0);
        v3 = new Vertex(0,0,0);
        color = Color.red;  // DEBUG eventually pass as a param? or leave null?
    }

    public Triangle(Vertex v1, Vertex v2, Vertex v3, Color color) {
        this.v1 = v1;
        this.v2 = v2;
        this.v3 = v3;
        this.color = color;
    }
}
