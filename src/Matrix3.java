public class Matrix3 {
    // handles matrix-matrix and vextor-matrix math for 3D rotation
    // can't handle translation - this would need a 4x4 matrix

    double[] values;

    Matrix3(double[] values) {
        this.values = values;
    }

    Matrix3 multiply(Matrix3 other) {
        // multiply this matrix by other
        double[] result = new double[9];
        for (int row=0; row<3; row++) {
            for (int col=0; col<3; col++) {
                for (int i=0; i<3; i++) {
                    result[row*3 + col] += this.values[row*3 +i] * other.values[i*3 + col];
                }
            }
        }
        return new Matrix3(result);
    }

    void applyTo(Vertex input, Vertex output) {
        // applies this matrix as a transformation to a single vertex
        output.x = input.x * values[0] + input.y * values[3] + input.z * values[6];
        output.y = input.x * values[1] + input.y * values[4] + input.z * values[7];
        output.z = input.x * values[2] + input.y * values[5] + input.z * values[8];
    }

    Vertex applyTo(Vertex input) {
        return new Vertex(
                input.x * values[0] + input.y * values[3] + input.z * values[6],
                input.x * values[1] + input.y * values[4] + input.z * values[7],
                input.x * values[2] + input.y * values[5] + input.z * values[8]
        );
    }

    void applyTo(Triangle input, Triangle output) {
        // applies this matrix to each vertex in a triangle
        applyTo(input.v1, output.v1);
        applyTo(input.v2, output.v2);
        applyTo(input.v3, output.v3);
    }

    Triangle applyTo(Triangle input) {
        input.v1 = applyTo(input.v1);
        input.v1 = applyTo(input.v2);
        input.v1 = applyTo(input.v2);
        return new Triangle(applyTo(input.v1), applyTo(input.v2), applyTo(input.v3), input.color);
   }
}
