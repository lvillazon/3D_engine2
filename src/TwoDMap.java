public class TwoDMap {
    // 2d array used to represent the map of a simple maze
    // a zero at a given position indicates a space
    // and a 1 is a wall
    private int[][] maze;

    public TwoDMap() {
        maze = new int[][]{
                {1,1,1,1,1,1,1,1,1,1},
                {1,0,1,0,0,1,0,0,0,1},
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
        System.out.print("checking:"+x+","+y);
        if (x>=0 && x<width() && y>=0 && y<height()) {
            System.out.println("...is " + (maze[y][x]==1));
            return (maze[y][x] == 1);
        }
        return true;  // all locations outside the grid count as wall
    }

    public boolean isWall(double x, double y) {
        return isWall((int)x, (int)y);
    }
}
