import javax.swing.*;
import java.awt.*;
import java.awt.event.KeyEvent;

public class TopDownMapView extends JPanel {
    // shows the player in a 2D maze, including their current heading
    private double playerMapX, playerMapY;  // coords in the 2D map grid - double to allow smooth movement over the grid
    private double heading;  // direction player faces, in radians
    private TwoDMap maze;
    private final int PLAYER_SIZE = 10; // in pixels, as drawn on the map view
    private final int MAX_VIEW_RANGE = 8;  // number of grid squares to throw each ray

    public TopDownMapView() {
        maze = new TwoDMap();
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

    @Override
    public void paint(Graphics g) {
        super.paint(g);
        Graphics2D g2 = (Graphics2D) g;
        // draw the maze as a series of squares
        int grid_height = getHeight() / maze.height();
        int grid_width = getWidth() / maze.width();
        int gridSize = Math.min(grid_width, grid_height);
        g2.setColor(Color.black);
        for (int x = 0; x < maze.width(); x++) {
            for (int y = 0; y < maze.height(); y++) {
                if (maze.isWall(x, y)) {
                    g2.fillRect(x * gridSize, y * gridSize, gridSize-1, gridSize-1);
                }
            }
        }

        // draw the player position
        g2.setColor(Color.red);
        int playerScreenX = (int) (playerMapX * gridSize);
        int playerScreenY = (int) (playerMapY * gridSize);
        g2.fillRect(playerScreenX - PLAYER_SIZE / 2,
                playerScreenY - PLAYER_SIZE / 2,
                PLAYER_SIZE, PLAYER_SIZE);
        // draw heading vector
        g2.drawLine(playerScreenX, playerScreenY,
                (int) (playerScreenX + PLAYER_SIZE * 2 * Math.cos(heading)),
                (int) (playerScreenY - PLAYER_SIZE * 2 * Math.sin(heading))
        );

        // draw ray casts
        double rx = 0, ry = 0;
        double rayAngle = getHeading();
        double rayXOffset, rayYOffset;  // used to extend the ray cast across the grid
        double inverseTan = 1/Math.tan(rayAngle);
        // TODO check vertical intersection points too
        //1. find 2D map coords of ray intersection with the nearest horizontal grid line
        if (rayAngle<Math.PI) { // looking up
            System.out.println("up");
            // y coord is just the player y, rounded down to the nearest int (each map square is 1x1)
            ry = (int) getPlayerY();
            // x coord is calculated using the inverse Tan of the heading
            rx = (getPlayerY() - ry) * inverseTan + getPlayerX();
            // calculate offsets needed for the next (and subsequent) grid squares as we extend the ray cast
            rayYOffset = -1; // moving up one whole square
            rayXOffset = -rayYOffset * inverseTan;
        } else { // looking down
            System.out.println("down");
            // round up to the next grid line
            ry = (int) (getPlayerY() + 1);
            rx = (getPlayerY() - ry) * inverseTan + getPlayerX();
            rayYOffset = 1; // moving down one square
            rayXOffset = -rayYOffset * inverseTan;
        }
        // if we are facing directly left or right, the ray cannot intersect a horizontal grid line
        int viewDistance = 0;
        if (rayAngle == 0 || rayAngle == Math.PI) {
            viewDistance = MAX_VIEW_RANGE;
        }
        // look for an intersection with a map wall
        while (viewDistance < MAX_VIEW_RANGE && !maze.isWall(rx+rayXOffset/2, ry+rayYOffset/2)) {
            // DEBUG draw collision check points
            g2.fillRect((int) ((rx+rayXOffset/2) * gridSize), (int) ((ry+rayYOffset/2) * gridSize),10,10);
            ry += rayYOffset;
            rx += rayXOffset;
            viewDistance++;
        }

        g2.drawLine(playerScreenX, playerScreenY,
                (int) (rx * gridSize),
                (int) (ry * gridSize)
        );

        System.out.println("heading:"+getHeading()+" player:"+getPlayerX()+","+getPlayerY()+" ray:"+rx+","+ry+" dist:"+viewDistance);

    }


    class KeyboardHandler implements KeyEventDispatcher {
        public boolean dispatchKeyEvent(KeyEvent e) {
            if (e.getKeyCode() == KeyEvent.VK_W) {
                playerMapX += Math.cos(heading)/10;
                playerMapY -= Math.sin(heading)/10;
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