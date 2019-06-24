package main;

import entities.Camera;
import entities.Character;

import javax.swing.*;
import java.awt.*;
import java.awt.event.ComponentAdapter;
import java.awt.event.ComponentEvent;

public class ElwynnGraphics {
    private ElwynnJPanel elwynnJPanel;
    private JFrame frame;

    public void createJFrame() {
        //create JFrame Instance
        frame = new JFrame();
        frame.setTitle("ElwynnJFrame");
        frame.setLayout(new BorderLayout());

        frame.setSize(new Dimension(Parameters.getInstance().getWindowWidth(), Parameters.getInstance().getWindowHeight()));
        frame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        frame.setVisible(true);
        //this will make the app to always display at the center
        frame.setLocationRelativeTo(null);
    }

    public void createJPanel() {
        elwynnJPanel = new ElwynnJPanel(Parameters.getInstance().getWindowWidth(), Parameters.getInstance().getWindowHeight());
        elwynnJPanel.setVisible(true);
        elwynnJPanel.addMyKeyListener();
        elwynnJPanel.addMyMouseListener();

        frame.addComponentListener(new ComponentAdapter() {
            public void componentResized(ComponentEvent componentEvent) {
                int newWidth = componentEvent.getComponent().getWidth();
                int newHeight = componentEvent.getComponent().getHeight();
                System.out.println("JFrame resized, newWidth: " + newWidth + ", newHeight: " + newHeight);
                Parameters.getInstance().setWindowWidth(newWidth);
                Parameters.getInstance().setWindowHeight(newHeight);
            }
        });
    }

    public void addJPanelToJFrame() {
        frame.add(elwynnJPanel);
    }

    public void updateFrame(long timeElapsed) {
        updateCamera(timeElapsed);
        elwynnJPanel.setTimeElapsed(timeElapsed);
        elwynnJPanel.repaint();
    }

    private void updateCamera(long timeElapsed) {
        double[] cameraVelocityVector = new double[2];
        cameraVelocityVector[0] = Character.getInstance().getCurrentCoordinates().getxCoordinate() - Camera.getInstance().getCoordinates().getxCoordinate();
        cameraVelocityVector[1] = Character.getInstance().getCurrentCoordinates().getyCoordinate() - Camera.getInstance().getCoordinates().getyCoordinate();
        double cameraSpeed = Utils.module(cameraVelocityVector) * 0.0025 * timeElapsed;
        cameraVelocityVector = Utils.normalizeVector(cameraVelocityVector);

        if (Double.isNaN(cameraVelocityVector[0]) || Double.isNaN(cameraVelocityVector[1])) {
            return;
        }

        Camera.getInstance().setCoordinates((int) (Camera.getInstance().getCoordinates().getxCoordinate() + (cameraVelocityVector[0] * cameraSpeed)),
                (int)(Camera.getInstance().getCoordinates().getyCoordinate() + (cameraVelocityVector[1] * cameraSpeed)));
    }
}
