import java.awt.*;

public class Texture {
    private int[][] array = {
            {1,1,0,0,1,1},
            {0,0,1,1,0,0},
            {1,1,0,0,1,1},
    };

    // return the texture value at x,y coords
    // uses modulo arithmetic to auto wrap the coords
    public Color valueAt(Color c, int x, int y) {
        if(array[Math.abs(y)%array.length][Math.abs(x)%array[0].length] == 1) {
            return c;
        } else {
            return Color.BLACK;
        }
    }
}
