import javax.swing.*;
import java.awt.*;
import java.awt.event.KeyEvent;
import java.awt.event.KeyListener;

public class TopDownMapView extends JPanel {
    // shows the player in a 2D maze, including their current heading
    private double playerMapX, playerMapY;  // coords in the 2D map grid - double to allow smooth movement over the grid
    private double heading;  // direction player faces, in radians
    private int playerSize = 10;
    private TwoDMap maze;
    private int grid_size;

    public TopDownMapView() {
        maze = new TwoDMap();
        playerMapX = 1.0;
        playerMapY = 1.0;
        playerSize = 10;
        heading = 0;
        KeyEventDispatcher myKeyEventDispatcher = new DefaultFocusManager();
        KeyboardFocusManager.getCurrentKeyboardFocusManager().addKeyEventDispatcher(new KeyboardHandler());

    }

    @Override
    public void paint(Graphics g) {
        super.paint(g);
        Graphics2D g2 = (Graphics2D) g;
        // draw the maze as a series of squares
        int grid_height = getHeight() / (maze.height());
        int grid_width = getWidth() / (2 * maze.width());
        int gridSize = Math.min(grid_width, grid_height);
        g2.setColor(Color.black);
        for (int x = 0; x < maze.width(); x++) {
            for (int y = 0; y < maze.height(); y++) {
                if (maze.isWall(x, y)) {
                    g2.fillRect(x * gridSize, y * gridSize, gridSize, gridSize);
                }
            }
        }

        // draw the player position
        g2.setColor(Color.red);
        int playerScreenX = (int) (playerMapX * gridSize + gridSize / 2);
        int playerScreenY = (int) (playerMapY * gridSize + gridSize / 2);
        g2.fillRect(playerScreenX - playerSize / 2,
                playerScreenY - playerSize / 2,
                playerSize, playerSize);
        // draw heading vector
        g2.drawLine(playerScreenX, playerScreenY,
                (int) (playerScreenX + playerSize * 2 * Math.sin(heading)),
                (int) (playerScreenY - playerSize * 2 * Math.cos(heading))
        );
    }
/*
    public void keyboardHandler(KeyEvent keyEvent) {
        int key = keyEvent.getKeyCode();
        if (key == keyEvent.VK_W) {
            playerMapX -= 0.1;
        }
        if (key == keyEvent.VK_S) {
            playerMapX += 0.1;
        }
        System.out.println(playerMapX);
    }


 */

    class KeyboardHandler implements KeyEventDispatcher {
        public boolean dispatchKeyEvent(KeyEvent e) {
            if (e.getKeyCode() == KeyEvent.VK_W) {
                playerMapX += Math.sin(heading)/10;
                playerMapY -= Math.cos(heading)/10;
            }
            if (e.getKeyCode() == KeyEvent.VK_S) {
                playerMapY += 0.1;
            }
            if (e.getKeyCode() == KeyEvent.VK_A) {
                heading -= 0.1;
            }
            if (e.getKeyCode() == KeyEvent.VK_D) {
                heading += 0.1;
            }
            repaint();
            e.consume(); // remove from event queue (probably not necessary)
            return true;  // instructs KeyboardFocusManager to take no further action on this key
        }
    }
}