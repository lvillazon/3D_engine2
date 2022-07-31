public class TwoDMap {
    // 2d array used to represent the map of a simple maze
    // a zero at a given position indicates a space
    // and a 1 is a wall
    private int[][] maze;

    public TwoDMap() {
        maze = new int[][]{
                {1,1,1,1,1,1,1,1,1,1},
                {1,0,1,0,0,0,0,0,0,1},
                {1,0,1,0,0,0,0,0,0,1},
                {1,0,1,0,0,0,0,0,0,1},
                {1,0,0,0,0,0,0,0,0,1},
                {1,1,1,1,0,0,0,0,0,1},
                {1,0,0,0,0,0,0,0,0,1},
                {1,0,1,0,0,0,0,0,0,1},
                {1,0,1,0,0,0,0,0,0,1},
                {1,1,1,1,1,1,1,1,1,1}
        };
    }

    public int height() {
        return maze.length;
    }

    public int width() {
        return maze[0].length;
    }

    public boolean isWall(int x, int y) {
        return (maze[y][x] == 1);
    }
}
