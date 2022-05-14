import javax.swing.*;
import java.awt.*;
import java.awt.image.BufferedImage;
import java.util.Arrays;

public class Viewer {
    public static final int LAPTOP = 0; // flags to set whether to display optimised for laptop or desktop
    public static final int DESKTOP = 1;
    private final JFrame frame;
    private final Container pane;
    private final JSlider horizontalSlider;
    private final JSlider verticalSlider;
    private final JPanel renderPanel;
    private int frameCount;          // used for fps calculation
    private long frameStart;         // used for fps calculation
    private long totalFrameDrawTime; // used for fps calculation
    private Profiler paintProfiler;  // track how long each part of the rendering takes
    private Profiler initProfiler;
    private Profiler allTrianglesProfiler;
    private Profiler triangleProfiler;
    private Profiler transformProfiler;
    private Profiler lightProfiler;
    private Profiler fillProfiler;
    private Profiler drawProfiler;
    private Tetrahedron shape;  // TODO expand this to an arraylist of shapes in the scene

    public Viewer(int monitorConfig) {
        frame = new JFrame();
        frame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        pane = frame.getContentPane();
        pane.setLayout(new BorderLayout());
        frameCount = 0;
        frameStart = 0;
        totalFrameDrawTime = 0;
        paintProfiler = new Profiler("paint", 100);
        initProfiler = paintProfiler.addSubProfiler("init");
        allTrianglesProfiler = paintProfiler.addSubProfiler("triangles");
        triangleProfiler = allTrianglesProfiler.addSubProfiler("triangles");
        transformProfiler = triangleProfiler.addSubProfiler("transform");
        lightProfiler = triangleProfiler.addSubProfiler("light");
        fillProfiler = triangleProfiler.addSubProfiler("fill");
        drawProfiler = triangleProfiler.addSubProfiler("draw");

        // sliders for rotation control
        horizontalSlider = new JSlider(0, 360, 180);
        pane.add(horizontalSlider, BorderLayout.SOUTH);
        verticalSlider = new JSlider(SwingConstants.VERTICAL, -90, 90, 0);
        pane.add(verticalSlider, BorderLayout.EAST);

        // panel for render output
        renderPanel = new JPanel() {
            public void paintComponent(Graphics g) {
                // just calls the render method
                // all the other code is concerned with measuring performance
                paintProfiler.start();
                render(g);
                paintProfiler.stop();
                paintProfiler.print();
                triangleProfiler.println();
            }
        };

        // action listeners to force redraw when the GUI updates
        horizontalSlider.addChangeListener(e -> renderPanel.repaint());
        verticalSlider.addChangeListener(e -> renderPanel.repaint());

        pane.add(renderPanel, BorderLayout.CENTER);
        if (monitorConfig == LAPTOP) {
            frame.setSize(1616, 876);  // full screen on my laptop
            frame.setLocation(160, 1072);  // position for laptop when running dual monitors
        } else {
            frame.setSize(800, 600);
            frame.setLocation(0, 0);       // normal position - top left of screen
        }
        frame.setVisible(true);
    }

    public void addShape(Tetrahedron shape) {
        this.shape = shape;
    }

    public void redraw() {
        //force a manual redraw
        renderPanel.repaint();
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
        initProfiler.start();
        Graphics2D g2 = (Graphics2D) g;
        BufferedImage buffer = new BufferedImage(renderPanel.getWidth(),
                                                 renderPanel.getHeight(),
                                                 BufferedImage.TYPE_INT_ARGB);
        double[] zBuffer = new double[buffer.getWidth() * buffer.getHeight()];
        // initialise depth buffer with the furthest possible values
        Arrays.fill(zBuffer, Double.NEGATIVE_INFINITY);

        // get rotation values from the GUI sliders
        double heading = Math.toRadians(horizontalSlider.getValue());
        double pitch = Math.toRadians(verticalSlider.getValue());

        // build a transformation matrix for the current rotation
        Matrix3 transform = getTransform(heading, pitch);
        Triangle t = new Triangle();
        Vertex v1,v2,v3;
        initProfiler.stop();
        allTrianglesProfiler.start();
        
        for (Triangle q: shape.getTriangles()) {
            triangleProfiler.start();
            transformProfiler.start();
            // rotate
            transform.applyTo(q, t);
            //t = transform.applyTo(t);
            v1 = t.v1;
            v2 = t.v2;
            v3 = t.v3;
//            Vertex v1 = transform.applyTo(t.v1);
//            Vertex v2 = transform.applyTo(t.v2);
//            Vertex v3 = transform.applyTo(t.v3);

            // translate based on slider values
            int xOffset = (int)(
                    ((double)horizontalSlider.getValue()/horizontalSlider.getMaximum())
                            * frame.getWidth());
            int zOffset = verticalSlider.getValue()*100;
            v1.x += xOffset;
            v1.y += renderPanel.getHeight()/2.0;
            v1.z += zOffset;
            v2.x += xOffset;
            v2.y += renderPanel.getHeight()/2.0;
            v2.z += zOffset;
            v3.x += xOffset;
            v3.y += renderPanel.getHeight()/2.0;
            v3.z += zOffset;

            // translate to the middle of the view
            /*
            v1.x += renderPanel.getWidth()/2.0;
            v1.y += renderPanel.getHeight()/2.0;
            v2.x += renderPanel.getWidth()/2.0;
            v2.y += renderPanel.getHeight()/2.0;
            v3.x += renderPanel.getWidth()/2.0;
            v3.y += renderPanel.getHeight()/2.0;
*/
            transformProfiler.stop();

            lightProfiler.start();
            // calculate vector cross product to get lighting angle
            Vertex norm = new Vertex(
                    v1.y * v2.z - v1.z * v2.y,
                    v1.z * v2.x - v1.x * v2.z,
                    v1.x * v2.y - v1.y * v2.x
            );
            double normalLength =
                    Math.sqrt(norm.x * norm.x + norm.y * norm.y + norm.z * norm.z);
            norm.x /= normalLength;
            norm.y /= normalLength;
            norm.z /= normalLength;
            double angleCos = Math.abs(norm.z);

            Color tcol = getShade(t.color, angleCos);
            lightProfiler.stop();

            fillProfiler.start();
            // compute rectangular bounds for the triangle
            // TODO this does not take z coord into account, so shapes do not get smaller as they get further away
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
                        buffer.setRGB(x, y, tcol.getRGB());
                        zBuffer[zIndex] = depth; // update new closest point at this location
                    }
                }
            }
            fillProfiler.stop();

            drawProfiler.start();
            // black previous frame
            g2.setColor(Color.BLACK);
            g2.fillRect(0, 0, renderPanel.getWidth(), renderPanel.getHeight());
            // draw new frame
            g2.drawImage(buffer, 0, 0, null);
            drawProfiler.stop();
            triangleProfiler.stop();
        }
        allTrianglesProfiler.stop();
    }

    public static Color getShade(Color color, double shade) {
        double redLinear = Math.pow(color.getRed(), 2.4) * shade;
        double greenLinear = Math.pow(color.getGreen(), 2.4) * shade;
        double blueLinear = Math.pow(color.getBlue(), 2.4) * shade;

        int red = (int) Math.pow(redLinear, 1/2.4);
        int green = (int) Math.pow(greenLinear, 1/2.4);
        int blue = (int) Math.pow(blueLinear, 1/2.4);

        return new Color(red, green, blue);
    }
}

