package inventory;

import main.*;

import java.util.ArrayList;

import static org.lwjgl.opengl.GL11.*;
import static org.lwjgl.opengl.GL11.GL_TEXTURE_2D;

public class Inventory {
    private static Inventory instance = null;
    private static int width = 0;
    private static int height = 0;

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

    public static Inventory getInstance() {
        if (instance == null) {
            instance = new Inventory();
        }
        return instance;
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
    }

    public ArrayList<InventorySlot> getListOfSlots() {
        return listOfSlots;
    }
}
