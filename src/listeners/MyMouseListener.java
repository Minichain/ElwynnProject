package listeners;

import java.awt.event.*;

import entities.Camera;
import entities.Scene;
import entities.Tree;
import main.Coordinates;
import main.Parameters;

public class MyMouseListener implements MouseListener, MouseMotionListener, MouseWheelListener {
    private static MyMouseListener instance = null;
    private int mousePositionX;
    private int mousePositionY;

    private MyMouseListener() {
    }

    public static MyMouseListener getInstance() {
        if (instance == null) {
            instance = new MyMouseListener();
        }
        return instance;
    }

    public void mousePressed(MouseEvent e) {
        System.out.println("mousePressed event at " + e.getX() + ", " + e.getY());
//        System.out.println("New tree created at " + e.getX() + ", " + e.getY());
//        Tree newTree = new Tree(e.getX() + (int)Camera.getInstance().getCoordinates().getxCoordinate() - (Parameters.getInstance().getWindowWidth() / 2),
//                e.getY() + (int)Camera.getInstance().getCoordinates().getyCoordinate() - (Parameters.getInstance().getWindowHeight() / 2));
//        Scene.getInstance().getListOfEntities().add(newTree);

        Coordinates mouseCoordinates = new Coordinates(e.getX(), e.getY());
        double[] mouseGlobalCoordinates = mouseCoordinates.toGlobalCoordinates();
        int x = (int) Math.floor(mouseGlobalCoordinates[0] / Parameters.getTilesSizeX());
        int y = (int) Math.floor(mouseGlobalCoordinates[1] / Parameters.getTilesSizeY());
        Scene.getInstance().setTile(x, y, (byte) 3);
    }

    public void mouseReleased(MouseEvent e) {
//        System.out.println("mouseReleased event: " + e);
    }

    public void mouseEntered(MouseEvent e) {
//        System.out.println("mouseEntered event: " + e);
    }

    public void mouseExited(MouseEvent e) {
//        System.out.println("mouseExited event: " + e);
    }

    public void mouseClicked(MouseEvent e) {
//        System.out.println("mouseClicked event: " + e);
    }

    public void mouseDragged(MouseEvent e) {
    }

    public void mouseMoved(MouseEvent e) {
        this.mousePositionX = e.getX();
        this.mousePositionY = e.getY();
    }

    public int getMousePositionX() {
        return this.mousePositionX;
    }

    public int getMousePositionY() {
        return this.mousePositionY;
    }

    @Override
    public void mouseWheelMoved(MouseWheelEvent e) {

    }
}