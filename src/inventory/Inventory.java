package inventory;

import items.Item;
import main.*;

import java.util.ArrayList;

import static org.lwjgl.opengl.GL11.*;
import static org.lwjgl.opengl.GL11.GL_TEXTURE_2D;

public class Inventory {
    private int width = 0;
    private int height = 0;
    private boolean opened;
    private Coordinates coordinates;
    private ArrayList<InventorySlot> listOfSlots;

    public Inventory() {
        opened = false;
        coordinates = new Coordinates(0, 0);
        listOfSlots = new ArrayList<>();
        int numberOfSlots = 9;
        for (int i = 0; i < numberOfSlots; i++) {
            listOfSlots.add(new InventorySlot());
        }
    }

    public boolean isOpened() {
        return opened;
    }

    public void setOpened(boolean opened) {
        Log.l("Inventory opened: " + opened);
        this.opened = opened;
    }

    public void update(long timeElapsed) {
        int numberOfSlots, numberOfRows, row;
        numberOfSlots = listOfSlots.size();
        numberOfRows = (int) Math.ceil(numberOfSlots / 4f);
        coordinates = new Coordinates(Window.getWidth() / 2f + (int) (350f * Parameters.getResolutionFactor()),
                Window.getHeight() - (int) ((numberOfRows * InventorySlot.getHeight() * 1.5f) + InventorySlot.getHeight() * 2));
        width = (int) (InventorySlot.getWidth() * 6.5f);
        height = (int) (numberOfRows * InventorySlot.getHeight() + (numberOfRows + 1) * (InventorySlot.getHeight() / 2f));
        double x, y;
        for (int i = 0; i < numberOfSlots; i++) {
            row = (int) Math.ceil((i + 1) / 4f);
            x = coordinates.x + InventorySlot.getWidth() / 2f + i * (InventorySlot.getWidth() * 1.5f) - (row - 1) * (InventorySlot.getWidth() * (4f + 4f / 2));
            y = coordinates.y + InventorySlot.getHeight() / 2f + (row - 1) * (InventorySlot.getWidth() * 1.5f);
            listOfSlots.get(i).setCoordinates(x, y);
            listOfSlots.get(i).update(timeElapsed);
        }
    }

    public void render() {
        glDisable(GL_TEXTURE_2D);
        OpenGLManager.glBegin(GL_TRIANGLES);
        OpenGLManager.drawRectangle((int) coordinates.x, (int) coordinates.y, width, height, 0.8, 0.2f);
        for (InventorySlot slot : listOfSlots) {
            slot.render();
        }
        glEnd();

        for (InventorySlot slot : listOfSlots) {
            slot.renderStoredItem();
        }
    }

    public ArrayList<InventorySlot> getListOfSlots() {
        return listOfSlots;
    }

    public Item isItemStored(Class<?> type) {
        for (int i = 0; i < listOfSlots.size(); i++) {
            if (type == listOfSlots.get(i).getStoredItem().getClass()) {
                return listOfSlots.get(i).getStoredItem();
            }
        }
        return null;
    }

    public void storeItem(Item item) {
        for (int i = 0; i < listOfSlots.size(); i++) {
            if (listOfSlots.get(i).getStoredItem() == null) {
                listOfSlots.get(i).storeItem(item);
                break;
            }
        }
    }

    public boolean removeItem(Class<?> type) {
        Item item;
        for (int i = listOfSlots.size() - 1; i > -1; i--) {
            item = listOfSlots.get(i).getStoredItem();
            if (item != null && type == item.getClass()) {
                listOfSlots.get(i).storeItem(null);
                return true;
            }
        }
        return false;
    }

    public int getAmountOfItemType(Class<?> type) {
        int amount = 0;
        Item item;
        for (int i = 0; i < listOfSlots.size(); i++) {
            item = listOfSlots.get(i).getStoredItem();
            if (item != null && type == item.getClass()) amount++;
        }
        return amount;
    }

    public ArrayList<Item> getListOfItems() {
        ArrayList<Item> listOfItems = new ArrayList<>();
        for (InventorySlot slot : listOfSlots) {
            if (slot.getStoredItem() != null) {
                listOfItems.add(slot.getStoredItem());
            }
        }
        return listOfItems;
    }

    public boolean isFreeSlot() {
        for (InventorySlot slot : listOfSlots) {
            if (slot.getStoredItem() == null) {
                return true;
            }
        }
        return false;
    }
}
