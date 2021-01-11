package inventory;

import main.Coordinates;
import main.OpenGLManager;
import main.Parameters;

public class InventorySlot {
    private Coordinates coordinates;
    private static int width = 0;
    private static int height = 0;

    public InventorySlot() {
        coordinates = new Coordinates(0, 0);
    }

    public void update(long timeElapsed) {
        setWidth((int) (50f * Parameters.getResolutionFactor()));
        setHeight((int) (50f * Parameters.getResolutionFactor()));
    }

    public void render() {
        OpenGLManager.drawRectangle((int) coordinates.x, (int) coordinates.y, width, height, 0.8, 0.4f);
    }

    public void setCoordinates(double x, double y) {
        coordinates.x = x;
        coordinates.y = y;
    }

    public static int getWidth() {
        return width;
    }

    private static void setWidth(int width) {
        InventorySlot.width = width;
    }

    public static int getHeight() {
        return height;
    }

    private static void setHeight(int height) {
        InventorySlot.height = height;
    }
}
