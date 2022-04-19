public class Vertex {
    // x,y,z coords
    // fields are package private, because this is just a tuple class - no encapsulation needed
    double x;
    double y;
    double z;

    public Vertex(double x, double y, double z) {
        this.x = x;
        this.y = y;
        this.z = z;
    }
/*
    public Vertex normal() {
        // return normal of this vector - ie crossproduct
    }

 */
}
