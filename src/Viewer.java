import javax.swing.*;
import java.awt.*;
import java.awt.geom.Path2D;
import java.awt.image.BufferedImage;

public class Viewer {
    private JFrame frame;
    private Container pane;
    private JSlider horizontalSlider;
    private JSlider verticalSlider;
    private JPanel renderPanel;
    private Tetrahedron shape;  // TODO expand this to an arraylist of shapes in the scene

    public Viewer() {
        frame = new JFrame();
        frame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        pane = frame.getContentPane();
        pane.setLayout(new BorderLayout());

        // sliders for rotation control
        horizontalSlider = new JSlider(0, 360, 180);
        pane.add(horizontalSlider, BorderLayout.SOUTH);
        verticalSlider = new JSlider(SwingConstants.VERTICAL, -90, 90, 0);
        pane.add(verticalSlider, BorderLayout.EAST);

        // panel for render output
        renderPanel = new JPanel() {
            public void paintComponent(Graphics g) {
                render(g);
            }
        };

        // action listeners to force redraw when the GUI updates
        horizontalSlider.addChangeListener(e -> renderPanel.repaint());
        verticalSlider.addChangeListener(e -> renderPanel.repaint());

        pane.add(renderPanel, BorderLayout.CENTER);
        frame.setSize(400,400);
        frame.setVisible(true);
    }

    public void addShape(Tetrahedron shape) {
        this.shape = shape;
    }

    private void old_render(Graphics g) {
        Graphics2D g2 = (Graphics2D) g;
        g2.setColor(Color.BLACK);
        g2.fillRect(0, 0, renderPanel.getWidth(), renderPanel.getHeight());

        // get rotation values from the GUI sliders
        double heading = Math.toRadians(horizontalSlider.getValue());
        double pitch = Math.toRadians(verticalSlider.getValue());

        // build a transformation matrix for the current rotation
        Matrix3 headingTransform = new Matrix3(new double [] {
                Math.cos(heading), 0, -Math.sin(heading),
                0, 1, 0,
                Math.sin(heading), 0, Math.cos(heading)
        });
        Matrix3 pitchTransform = new Matrix3((new double[] {
                1, 0, 0,
                0, Math.cos(pitch), Math.sin(pitch),
                0, -Math.sin(pitch), Math.cos(pitch)
        }));
        Matrix3 transform = headingTransform.multiply(pitchTransform);

        // rendering all objects
        shape.render(g2, renderPanel.getWidth()/2, renderPanel.getHeight()/2, transform);
    }

    private Matrix3 getTransform(double heading, double pitch) {
        Matrix3 headingTransform = new Matrix3(new double [] {
                Math.cos(heading), 0, -Math.sin(heading),
                0, 1, 0,
                Math.sin(heading), 0, Math.cos(heading)
        });
        Matrix3 pitchTransform = new Matrix3((new double[] {
                1, 0, 0,
                0, Math.cos(pitch), Math.sin(pitch),
                0, -Math.sin(pitch), Math.cos(pitch)
        }));
        return headingTransform.multiply(pitchTransform);
    }

    private void render(Graphics g) {
        Graphics2D g2 = (Graphics2D) g;
        BufferedImage buffer = new BufferedImage(renderPanel.getWidth(),
                                                 renderPanel.getHeight(),
                                                 BufferedImage.TYPE_INT_ARGB);
        double[] zBuffer = new double[buffer.getWidth() * buffer.getHeight()];
        // initialise depth buffer with furthest possible values
        for (int i=0; i<zBuffer.length; i++) {
            zBuffer[i] = Double.NEGATIVE_INFINITY;
        }

        // get rotation values from the GUI sliders
        double heading = Math.toRadians(horizontalSlider.getValue());
        double pitch = Math.toRadians(verticalSlider.getValue());

        // build a transformation matrix for the current rotation
        Matrix3 transform = getTransform(heading, pitch);

        for (Triangle t: shape.getTriangles()) {
            // rotate
            Vertex v1 = transform.transform(t.v1);
            Vertex v2 = transform.transform(t.v2);
            Vertex v3 = transform.transform(t.v3);

            // translate to the middle of the view
            v1.x += renderPanel.getWidth()/2;
            v1.y += renderPanel.getHeight()/2;
            v2.x += renderPanel.getWidth()/2;
            v2.y += renderPanel.getHeight()/2;
            v3.x += renderPanel.getWidth()/2;
            v3.y += renderPanel.getHeight()/2;

            // compute rectangular bounds for the triangle
            int minX = (int) Math.max(0, Math.ceil(Math.min(v1.x, Math.min(v2.x, v3.x))));
            int maxX = (int) Math.min(buffer.getWidth()-1, Math.floor(Math.max(v1.x, Math.max(v2.x, v3.x))));
            int minY = (int) Math.max(0, Math.ceil(Math.min(v1.y, Math.min(v2.y, v3.y))));
            int maxY = (int) Math.min(buffer.getHeight()-1, Math.floor(Math.max(v1.y, Math.max(v2.y, v3.y))));

            double triangleArea = (v1.y - v3.y) * (v2.x - v3.x) + (v2.y - v3.y) * (v3.x - v1.x);

            // draw all points that fall inside the triangle
            // this uses barycentric coordinate rasterization - not the most efficient, but doesn't require
            // accessing the GPU pipeline, so it's simpler to code
            for (int y=minY; y<=maxY; y++) {
                for (int x = minX; x <= maxX; x++) {
                    double b1 = ((y - v3.y) * (v2.x - v3.x) + (v2.y - v3.y) * (v3.x - x)) / triangleArea;
                    double b2 = ((y - v1.y) * (v3.x - v1.x) + (v3.y - v1.y) * (v1.x - x)) / triangleArea;
                    double b3 = ((y - v2.y) * (v1.x - v2.x) + (v1.y - v2.y) * (v2.x - x)) / triangleArea;
                    // compute depth for z buffering
                    double depth = b1 * v1.z + b2 * v2.z + b3 * v3.z;
                    int zIndex = y * buffer.getWidth() + x;
                    if (b1 >= 0 && b1 <= 1 && b2 >= 0 && b2 <= 1 && b3 >= 0 && b3 <= 1 && zBuffer[zIndex]<depth) {
                        buffer.setRGB(x, y, t.color.getRGB());
                        zBuffer[zIndex] = depth; // update new closest point at this location
                    }
                }
            }
            // black previous frame
            g2.setColor(Color.BLACK);
            g2.fillRect(0, 0, renderPanel.getWidth(), renderPanel.getHeight());
            // draw new frame
            g2.drawImage(buffer, 0, 0, null);
        }
    }
}

