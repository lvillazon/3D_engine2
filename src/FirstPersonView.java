import javax.swing.*;
import java.awt.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.ComponentAdapter;
import java.awt.event.ComponentEvent;

public class FirstPersonView extends JPanel {
    // shows the 3D view in the maze, from the player POV
    private TopDownMapView map;
    private Timer frameTimer;
    private final double ONE_DEGREE = 0.017453;  // in radians
    private final double RESOLUTION = 0.4;  // angular resolution in degrees
    private final int VIEW_ANGLE = 60; // in degrees;
    private final Color brightColor;
    private final Color darkColor;
    private final Color skyColor;
    private final Color floorColor;
    private final Texture wallTexture;

    public FirstPersonView(TopDownMapView map) {
        brightColor = new Color(220, 0, 0);
        darkColor = new Color(160, 0, 0);
        skyColor = new Color(0, 50, 200);
        floorColor = new Color(160, 160, 160);
        wallTexture = new Texture();

        this.map = map;

        addComponentListener(new ComponentAdapter() {
            public void componentResized(ComponentEvent e) {
                setPreferredSize(new Dimension(getParent().getWidth()/2, getParent().getHeight()));
                revalidate();
            }
        });

        frameTimer = new Timer(1000/60, new ActionListener() { // fire 60 times per second
            @Override
            public void actionPerformed(ActionEvent e) {
                repaint();
            }
        });
        frameTimer.start();
    }

    @Override
    public void paint(Graphics g) {
        super.paint(g);
        Graphics2D g2 = (Graphics2D) g;
        // draw sky and floor rectangles
        int floorLevel = (int)(getHeight()*.86);
        g2.setColor(skyColor);
        g2.fillRect(0,0, getWidth(), floorLevel);
        g2.setColor(floorColor);
        g2.fillRect(0,floorLevel, getWidth(), getHeight());

        // draw the current view in the maze, using raycasting
        double rayAngle = map.getHeading() - ONE_DEGREE * VIEW_ANGLE/2; // cast rays from either side of player heading
        if (rayAngle < 0) {
            rayAngle += 2*Math.PI;
        }
        int vScale = getHeight()/3;
        int hScale = (int)(getWidth() / VIEW_ANGLE * RESOLUTION );
        g2.setStroke(new BasicStroke(hScale));
        double i=0;
        // quit rendering if the window is ever so small that hScale is 0
        while (i<getWidth() + hScale && hScale>0) {
            double rayLength = map.rayCaster.cast(map.getPlayerX(), map.getPlayerY(), rayAngle);

            // fix fish-eye distortion
            double relativeAngle = map.getHeading() - rayAngle;
            if (relativeAngle <0) {
                relativeAngle += Math.PI * 2;
            }
            if (relativeAngle > Math.PI * 2) {
                relativeAngle -= Math.PI * 2;
            }
            rayLength = rayLength * Math.cos(relativeAngle);

            // draw vertical wall segment with a height inversely proportional to the raycast distance
            int segmentHeight = (int)(1/rayLength * vScale);
            int segmentXPos = (int)(getWidth() - i);
            if (map.rayCaster.inShadow()) {
                drawVLine(g2, darkColor, segmentXPos, floorLevel, segmentHeight);
            } else {
                drawVLine(g2, brightColor, segmentXPos, floorLevel, segmentHeight);
            }


            // cast a new ray, 1 degree over
            rayAngle += ONE_DEGREE * RESOLUTION;
            if (rayAngle > 2*Math.PI) {
                rayAngle -= 2*Math.PI;
            }
            i += hScale;
        }

    }

    private void drawVLine(Graphics2D g2, Color baseColor, int x, int bottom, int length) {
        double textureY = 0.0;
        //double textureX = ??? how do we work this out?;
        double textureYStep = 32.0/length;
        for (int y=bottom; y>bottom-length; y--) {
            Color c = wallTexture.valueAt(baseColor, 0, (int)(textureY));
            g2.setColor(c);
            g2.drawLine(x, y, x, y);  // Java lacks a point draw routine, so we draw a line of 1 pixel
            textureY += textureYStep;
        }
    }
}