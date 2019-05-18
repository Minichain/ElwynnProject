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
        Camera.getInstance().setCoordinates((int) Character.getInstance().getCurrentCoordinates().getxCoordinate(), (int)Character.getInstance().getCurrentCoordinates().getyCoordinate());
        elwynnJPanel.setTimeElapsed(timeElapsed);
        elwynnJPanel.repaint();
    }
}
