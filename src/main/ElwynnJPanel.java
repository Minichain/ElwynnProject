package main;

import entities.Camera;
import entities.Character;
import entities.Scene;
import entities.Tree;
import listeners.MyKeyListener;
import listeners.MyMouseListener;

import javax.swing.*;
import java.awt.*;
import java.util.List;

public class ElwynnJPanel extends JPanel {
    private long timeElapsed;
    private long fps;

    ElwynnJPanel(int width, int height) {
        setPreferredSize(new Dimension(width, height));
    }

    public void addMyKeyListener() {
        addKeyListener(MyKeyListener.getInstance());
        setFocusable(true);
    }

    public void addMyMouseListener() {
        addMouseListener(MyMouseListener.getInstance());
        setFocusable(true);
    }

    public void setTimeElapsed(long te) {
        timeElapsed = te;
        fps = 1000 / timeElapsed;
    }

    public void paint(Graphics g){
        super.paint(g); // cleans the panel

        double[] localCoordinates;
        localCoordinates = Scene.getInstance().getCoordinates().toLocalCoordinates();
        g.drawImage(Scene.getInstance().getSprite(),
                (int)localCoordinates[0],
                (int)localCoordinates[1],
                Scene.getInstance().getSpriteWidth(),
                Scene.getInstance().getSpriteHeight(),
                null);

        Tree tree;
        List<Tree> listOfTrees = Scene.getInstance().getListOfTrees();
        for (int i = 0; i < listOfTrees.size(); i++) {
            tree = listOfTrees.get(i);
//            System.out.println("Painting tree Nº " + i + " at (" + tree.getCoordinates().getxCoordinate() + ", " + tree.getCoordinates().getyCoordinate()
//                    + "). Width: " + tree.getSprite().getWidth() + ", Height: " + tree.getSprite().getHeight());
            localCoordinates = tree.getCoordinates().toLocalCoordinates();
            g.drawImage(tree.getSprite(),
                    (int)localCoordinates[0]
                            - (tree.getSprite().getWidth() / 2),
                    (int)localCoordinates[1]
                            - (tree.getSprite().getHeight() / 2),
                    tree.getSprite().getWidth(),
                    tree.getSprite().getHeight(),
                    null);
        }

        if (Parameters.getInstance().isDebugMode()) {
            localCoordinates = Scene.getCoordinates().toLocalCoordinates();
            g.drawImage(Scene.getInstance().getCollisionsMap(),
                    (int)localCoordinates[0],
                    (int)localCoordinates[1],
                    (Scene.getInstance().getSpriteWidth()),
                    (Scene.getInstance().getSpriteHeight()),
                    null);

            g.drawLine(0, Parameters.getInstance().getWindowHeight()/2,
                    Parameters.getInstance().getWindowWidth(), Parameters.getInstance().getWindowHeight()/2);
            g.drawLine(Parameters.getInstance().getWindowWidth()/2, 0,
                    Parameters.getInstance().getWindowWidth()/2, Parameters.getInstance().getWindowHeight());
            g.drawString("FPS: " + fps, 10, 20);
            g.drawString("X: " + (int) Character.getInstance().getCurrentCoordinates().getxCoordinate(), 10, 35);
            g.drawString("Y: " + (int)Character.getInstance().getCurrentCoordinates().getyCoordinate(), 10, 50);
        }

        localCoordinates = Character.getInstance().getCurrentCoordinates().toLocalCoordinates();
        g.drawImage(Character.getInstance().getSprite(),
                (int)localCoordinates[0]
                        - (Character.getInstance().getSpriteWidth() / 2),
                (int)localCoordinates[1]
                        - (Character.getInstance().getSpriteHeight() / 2),
                (int)(Character.getInstance().getSpriteWidth() * Character.getInstance().getScale()),
                (int)(Character.getInstance().getSpriteHeight() * Character.getInstance().getScale()),
                null);
    }
}