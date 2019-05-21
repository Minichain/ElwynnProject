package main;

import entities.Camera;
import entities.Character;
import entities.Entity;
import entities.Scene;
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

        /** SCENE BACKGROUND IS DRAWN FIRST **/
        localCoordinates = Scene.getInstance().getCoordinates().toLocalCoordinates();
        g.drawImage(Scene.getInstance().getSprite(),
                (int)localCoordinates[0],
                (int)localCoordinates[1],
                Scene.getInstance().getSpriteWidth(),
                Scene.getInstance().getSpriteHeight(),
                null);


        /** ALL ENTITES ARE DRAWN BY ORDER OF DEPTH **/
        Entity entity;
        List<Entity> listOfEntities = Scene.getInstance().getListOfEntities();
        for (int i = 0; i < listOfEntities.size(); i++) {
            entity = listOfEntities.get(i);
//            System.out.println("Painting tree Nº " + (i + 1)
//                    + " at (" + entity.getCoordinates().getxCoordinate()
//                    + ", " + entity.getCoordinates().getyCoordinate());
            localCoordinates = entity.getCoordinates().toLocalCoordinates();
            int renderDistance = 1500;  //TODO This should depend on the Window and Camera width/height
            if (Utils.module(Camera.getInstance().getCoordinates(), entity.getCoordinates()) < renderDistance) {
                g.drawImage(entity.getSprite(),
                        (int)localCoordinates[0]
                                - (entity.getSprite().getWidth() / 2),
                        (int)localCoordinates[1]
                                - (int)(entity.getSprite().getHeight() * 0.75),
                        entity.getSprite().getWidth(),
                        entity.getSprite().getHeight(),
                        null);
            }
        }


        /** DEBUG ELEMENTS **/
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
            g.drawString("Number of entities: " + Scene.getInstance().getListOfEntities().size(), 10, 65);
        }
    }
}