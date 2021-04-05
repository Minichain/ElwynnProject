package inventory;

import items.Item;
import main.Coordinates;
import main.OpenGLManager;
import main.Parameters;
import text.TextRendering;

public class InventorySlot {
    private Coordinates coordinates;
    private static int width = 0;
    private static int height = 0;
    private Item item = null;
    private int amount;

    public InventorySlot() {
        coordinates = new Coordinates(0, 0);
        amount = 0;
    }

    public void update(long timeElapsed) {
        setWidth((int) (50f * Parameters.getHeightResolutionFactor()));
        setHeight((int) (50f * Parameters.getHeightResolutionFactor()));
    }

    public void render() {
        OpenGLManager.drawRectangle((int) coordinates.x, (int) coordinates.y, width, height, 0.8, 0.4f);
    }

    public void renderStoredItem() {
        if (item != null) {
            int x = (int) coordinates.x + (getWidth() / 2);
            int y = (int) coordinates.y + (getHeight() / 2);
            item.getSprite().draw(x, y, 0, 0, 1f, 4.0 * Parameters.getHeightResolutionFactor(), true);
            if (amount > 1) {
                TextRendering.renderText(x, y, "x" + amount, 2f * Parameters.getHeightResolutionFactor());
            }
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
        this.amount++;
    }

    public void removeStoredItem() {
        removeStoredItem(1);
    }

    public void removeStoredItem(int amount) {
        this.amount -= amount;
        if (this.amount <= 0) {
            this.item = null;
        }
    }

    public Item getStoredItem() {
        return item;
    }

    public int getAmount() {
        return amount;
    }
}
