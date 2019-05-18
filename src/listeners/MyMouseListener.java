package listeners;

import java.awt.event.MouseEvent;
import java.awt.event.MouseListener;

import entities.Camera;
import entities.Scene;
import entities.Tree;
import main.Parameters;

public class MyMouseListener implements MouseListener {
    private static MyMouseListener instance = null;

    private MyMouseListener() {
    }

    public static MyMouseListener getInstance() {
        if (instance == null) {
            instance = new MyMouseListener();
        }
        return instance;
    }


    public void mousePressed(MouseEvent e) {
//        System.out.println("mousePressed event: " + e);
        System.out.println("New tree created at " + e.getX() + ", " + e.getY());
        Tree newTree = new Tree(e.getX() + (int)Camera.getInstance().getCoordinates().getxCoordinate() - (Parameters.getInstance().getWindowWidth() / 2),
                e.getY() + (int)Camera.getInstance().getCoordinates().getyCoordinate() - (Parameters.getInstance().getWindowHeight() / 2));
        Scene.getInstance().getListOfTrees().add(newTree);
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

}