public class RayCaster {
    // used to calculate raycasts from any point in a 2D map
    private TwoDMap walls;
    private final int MAX_VIEW_RANGE = 8;  // number of grid squares to throw each ray
    private boolean horizontalCollision;

    public RayCaster(TwoDMap walls) {
        this.walls = walls;
    }

    private double distanceBetween(double x1, double y1, double x2, double y2) {
        // pythagorean distance from (x1,y1) to (x2,y2)
        return Math.sqrt(Math.pow(x1-x2, 2) + Math.pow(y1-y2, 2));
    }

    public double cast(double fromX, double fromY, double rayAngle) {
        // cast a ray from (fromX, fromY) in the 2D map at the specified angle
        // and return the distance it travels before it hits something

        double inverseTan = 1 / Math.tan(rayAngle);
        double normalTan = Math.tan(rayAngle);
        double rx, ry;
        double rayXOffset, rayYOffset;  // used to extend the ray cast across the grid

        //1. find 2D map coords of ray intersection with the nearest horizontal grid line
        if (rayAngle < Math.PI) { // looking up
            // y coord is just the player y, rounded down to the nearest int (each map square is 1x1)
            ry = (int) fromY - 0.001; // subtract a tiny amount to ensure it rounds in the right direction
            // x coord is calculated using the inverse Tan of the heading
            rx = (fromY - ry) * inverseTan + fromX;
            // calculate offsets needed for the next (and subsequent) grid squares as we extend the ray cast
            rayYOffset = -1; // moving up one whole square
            rayXOffset = -rayYOffset * inverseTan;
        } else { // looking down
            // round up to the next grid line
            ry = (int) (fromY + 1);
            rx = (fromY - ry) * inverseTan + fromX;
            rayYOffset = 1; // moving down one square
            rayXOffset = -rayYOffset * inverseTan;
        }
        // if we are facing directly left or right, the ray cannot intersect a horizontal grid line
        int viewDistance = 0;
        if (rayAngle == 0 || rayAngle == Math.PI) {
            viewDistance = MAX_VIEW_RANGE;
        }
        // look for an intersection with a map wall
        while (viewDistance < MAX_VIEW_RANGE && !walls.isWall(rx, ry)) {
            ry += rayYOffset;
            rx += rayXOffset;
            viewDistance++;
        }
        // save this ray, so we can compare with the vertical grid collision ray
        double horizontalColliderRayX = rx;
        double horizontalColliderRayY = ry;

        //2. Repeat for vertical grid lines
        if (rayAngle > Math.PI / 2 && rayAngle < Math.PI * 3 / 2) { // looking left
            rx = (int) fromX - 0.001;
            ry = (fromX - rx) * normalTan + fromY;
            rayXOffset = -1;
            rayYOffset = -rayXOffset * normalTan;
        } else { // looking right
            rx = (int) (fromX + 1);
            ry = (fromX - rx) * normalTan + fromY;
            rayXOffset = 1;
            rayYOffset = -rayXOffset * normalTan;
        }
        // if we are facing directly up or down, the ray cannot intersect a vertical grid line
        viewDistance = 0;
        if (rayAngle == Math.PI / 2 || rayAngle == Math.PI * 3 / 2) {
            viewDistance = MAX_VIEW_RANGE;
        }
        // look for an intersection with a map wall
        while (viewDistance < MAX_VIEW_RANGE && !walls.isWall(rx, ry)) {
            ry += rayYOffset;
            rx += rayXOffset;
            viewDistance++;
        }

        // return the length of the shorter of the two rays (horizontal or vertical colliders)
        // and set a flag so that we can tell which collision occurred - this is used for a simple lighting check
        double hDistance = distanceBetween(fromX, fromY, horizontalColliderRayX, horizontalColliderRayY);
        double vDistance = distanceBetween(fromX, fromY, rx, ry);
        if (hDistance < vDistance) {
            horizontalCollision = true;
            return hDistance;
        } else {
            horizontalCollision = false;
            return vDistance;
        }
    }

    public boolean inShadow() {
        // v simple lighting check - vertically oriented walls (in the 2D map) are assumed to face
        // towards the light source, so horizontal walls are drawn in a darker shade;
        return horizontalCollision;
    }
}