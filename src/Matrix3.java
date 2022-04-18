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

    Vertex transform(Vertex input) {
        // applies this matrix as a transformation to the input vertex
        return new Vertex(
                input.x * values[0] + input.y * values[3] + input.z * values[6],
                input.x * values[1] + input.y * values[4] + input.z * values[7],
                input.x * values[2] + input.y * values[5] + input.z * values[8]
        );
    }
}
