package inventory;

import items.Item;
import main.Coordinates;
import main.OpenGLManager;
import main.Parameters;

public class InventorySlot {
    private Coordinates coordinates;
    private static int width = 0;
    private static int height = 0;
    private Item item = null;

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

    public void renderStoredItem() {
        if (item != null) {
            item.getSprite().draw((int) coordinates.x + (getWidth() / 2), (int) coordinates.y + (getHeight() / 2),
                    0, 0, 1f, 4.0 * Parameters.getResolutionFactor(), true);
        }
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

    public void storeItem(Item item) {
        this.item = item;
    }

    public Item getStoredItem() {
        return item;
    }
}
