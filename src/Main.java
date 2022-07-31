public class Main {

    public static void main(String[] args) {
        /*
        // test polyhedron rotation
        RotationViewer v = new RotationViewer();
        Window w = new Window(Window.LAPTOP, v);
        Tetrahedron t = new Tetrahedron(new Vertex(100, 100, 100),
                new Vertex(-100, -100, 100),
                new Vertex(-100, 100, -100),
                new Vertex(100, -100, -100)
        );
        v.addShape(t);
        while (true) {
            v.redraw();
        }

         */
        // test raycasting
        TopDownMapView map = new TopDownMapView();
        Window w = new Window(Window.LAPTOP, map);
    }
}
