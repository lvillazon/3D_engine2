import javax.swing.*;
import java.awt.*;
import java.awt.event.KeyEvent;

public class TopDownMapView extends JPanel {
    // shows the player in a 2D maze, including their current heading
    private double playerMapX, playerMapY;  // coords in the 2D map grid - double to allow smooth movement over the grid
    private double heading;  // direction player faces, in radians
    private TwoDMap maze;
    public RayCaster rayCaster;
    private final int PLAYER_SIZE = 10; // in pixels, as drawn on the map view
    private final double ONE_DEGREE = 0.017453;  // in radians

    public TopDownMapView() {
        maze = new TwoDMap();
        rayCaster = new RayCaster(maze);
        playerMapX = 1.5;
        playerMapY = 8.5;
        heading = Math.PI/2;  // straight up
        KeyboardFocusManager.getCurrentKeyboardFocusManager().addKeyEventDispatcher(new KeyboardHandler());
    }

    public double getHeading() {
        return heading;
    }

    public double getPlayerX() {
        return playerMapX;
    }

    public double getPlayerY() {
        return playerMapY;
    }

    private double distanceTo(double x, double y) {
        // pythagorean distance from player position to x,y
        return Math.sqrt(Math.pow(getPlayerX()-x, 2) + Math.pow(getPlayerY()-y, 2));
    }

    @Override
    public void paint(Graphics g) {
        super.paint(g);
        Graphics2D g2 = (Graphics2D) g;
        // draw the maze as a series of squares
        int grid_height = getHeight() / maze.height();
        int grid_width = getWidth() / maze.width();
        int gridSize = Math.min(grid_width, grid_height);
        for (int x = 0; x < maze.width(); x++) {
            for (int y = 0; y < maze.height(); y++) {
                if (maze.isWall(x, y)) {
                    g2.setColor(Color.black);
                    g2.fillRect(x * gridSize, y * gridSize, gridSize-1, gridSize-1);

                    // label wall squares with their map coords
                    /*
                    g2.setColor(Color.white);
                    String coordText = Integer.toString(x) + "," + Integer.toString(y);
                    g2.drawString(coordText, x*gridSize + 30, y*gridSize + gridSize/2);
                     */
                }
            }
        }

        int playerScreenX = (int) (playerMapX * gridSize);
        int playerScreenY = (int) (playerMapY * gridSize);

        // draw ray casts
        double rayAngle = getHeading() - ONE_DEGREE * 30; // cast rays at 1-deg increments from 30-deg either side
        if (rayAngle < 0) {
            rayAngle += 2*Math.PI;
        }
        g2.setColor(Color.red);
        g2.setStroke(new BasicStroke(1));
        for (int i=0; i<60; i++) {
            rayAngle += ONE_DEGREE;
            if (rayAngle > 2*Math.PI) {
                rayAngle -= 2*Math.PI;
            }
            double rayLength = rayCaster.cast(getPlayerX(), getPlayerY(), rayAngle);
            g2.drawLine(playerScreenX, playerScreenY,
                        (int) (playerScreenX + Math.cos(rayAngle) * rayLength * gridSize),
                        (int) (playerScreenY - Math.sin(rayAngle) * rayLength * gridSize));
            }

        // draw the player position
        g2.setColor(Color.green);
        g2.setStroke(new BasicStroke(3));
        g2.fillRect(playerScreenX - PLAYER_SIZE / 2,
                playerScreenY - PLAYER_SIZE / 2,
                PLAYER_SIZE, PLAYER_SIZE);

        // draw heading vector
        g2.drawLine(playerScreenX, playerScreenY,
                (int) (playerScreenX + PLAYER_SIZE * 2 * Math.cos(heading)),
                (int) (playerScreenY - PLAYER_SIZE * 2 * Math.sin(heading))
        );

    }

    class KeyboardHandler implements KeyEventDispatcher {
        public boolean dispatchKeyEvent(KeyEvent e) {
            if (e.getKeyCode() == KeyEvent.VK_W) {
                playerMapX += Math.cos(heading)/10;
                playerMapY -= Math.sin(heading)/10;
            }
            if (e.getKeyCode() == KeyEvent.VK_S) {
                playerMapX -= Math.cos(heading)/10;
                playerMapY += Math.sin(heading)/10;
            }
            if (e.getKeyCode() == KeyEvent.VK_A) {
                heading += 0.1;
                if (heading > Math.PI *2) {
                    heading = 0;
                }
            }
            if (e.getKeyCode() == KeyEvent.VK_D) {
                heading -= 0.1;
                if (heading <0) {
                    heading = Math.PI * 2;
                }
            }
            repaint();
            e.consume(); // remove from event queue (probably not necessary)
            return true;  // instructs KeyboardFocusManager to take no further action on this key
        }
    }

}