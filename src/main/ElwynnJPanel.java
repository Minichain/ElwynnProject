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
        addMouseMotionListener(MyMouseListener.getInstance());
        addMouseWheelListener(MyMouseListener.getInstance());
        setFocusable(true);
    }

    public void setTimeElapsed(long te) {
        timeElapsed = te;
        fps = 1000 / timeElapsed;
    }

    public void paint(Graphics g){
        super.paint(g); // cleans the panel
        Graphics2D graphics2D = (Graphics2D) g;

        int renderDistance = 1000;  //TODO This should depend on the Window and Camera width/height
        double[] localCoordinates;

        /** SCENE BACKGROUND IS DRAWN FIRST **/
        byte[][] arrayOfTyles = Scene.getInstance().getArrayOfTiles();
        localCoordinates = Scene.getInstance().getCenter().toLocalCoordinates();
        //TODO create the background from the tiles and paint it as one image
        for (int i = 0; i < Scene.getInstance().getSceneX(); i++) {
            for (int j = 0; j < Scene.getInstance().getSceneY(); j++) {
                int x = (i * Parameters.getTilesSizeX());
                int y = (j * Parameters.getTilesSizeY());
                if (Utils.module(Camera.getInstance().getCoordinates(), new Coordinates(x, y)) < renderDistance) {
                    graphics2D.drawImage(Scene.getInstance().getTile(arrayOfTyles[i][j]),
                            x + (int)localCoordinates[0],
                            y + (int)localCoordinates[1],
                            Parameters.getTilesSizeX(),
                            Parameters.getTilesSizeY(),
                            null);
                }
            }
        }

        /** ALL ENTITES ARE DRAWN BY ORDER OF DEPTH **/
        Entity entity;
        List<Entity> listOfEntities = Scene.getInstance().getListOfEntities();
        for (int i = 0; i < listOfEntities.size(); i++) {
            entity = listOfEntities.get(i);
//            System.out.println("Painting tree Nº " + (i + 1)
//                    + " at (" + entity.getCoordinates().getxCoordinate()
//                    + ", " + entity.getCoordinates().getyCoordinate());
            localCoordinates = entity.getCoordinates().toLocalCoordinates();
            if (Utils.module(Camera.getInstance().getCoordinates(), entity.getCoordinates()) < renderDistance) {
                graphics2D.drawImage(entity.getSprite(),
                        (int)localCoordinates[0]
                                - (entity.getSprite().getWidth() / 2),
                        (int)localCoordinates[1]
                                - (int)(entity.getSprite().getHeight() * 0.75),
                        entity.getSprite().getWidth(),
                        entity.getSprite().getHeight(),
                        null);

                /** DEBUG ELEMENTS **/
                if (Parameters.getInstance().isDebugMode()) {
                    int radius = 50;
                    graphics2D.drawOval((int)localCoordinates[0] - radius,
                            (int)localCoordinates[1] - radius,
                            radius * 2, radius * 2);
                }
            }
        }

        /** MOUSE POSITION **/
        int mouseX = MyMouseListener.getInstance().getMousePositionX();
        int mouseY = MyMouseListener.getInstance().getMousePositionY();
        graphics2D.drawImage(Scene.getInstance().getTile(MyMouseListener.getInstance().getMouseWheelPosition() % 4),
                mouseX,
                mouseY,
                Parameters.getTilesSizeX(),
                Parameters.getTilesSizeY(),
                null);

        /** DEBUG ELEMENTS **/
        if (Parameters.getInstance().isDebugMode()) {
            graphics2D.drawLine(0, Parameters.getInstance().getWindowHeight()/2,
                    Parameters.getInstance().getWindowWidth(), Parameters.getInstance().getWindowHeight()/2);
            graphics2D.drawLine(Parameters.getInstance().getWindowWidth()/2, 0,
                    Parameters.getInstance().getWindowWidth()/2, Parameters.getInstance().getWindowHeight());
            graphics2D.drawString("FPS: " + fps, 10, 20);
            graphics2D.drawString("X: " + (int) Character.getInstance().getCurrentCoordinates().getxCoordinate(), 10, 35);
            graphics2D.drawString("Y: " + (int)Character.getInstance().getCurrentCoordinates().getyCoordinate(), 10, 50);
            graphics2D.drawString("Number of entities: " + Scene.getInstance().getListOfEntities().size(), 10, 65);
        }
    }
}