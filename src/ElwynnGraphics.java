import javax.swing.*;
import java.awt.*;

public class ElwynnGraphics {
    private ElwynnJPanel elwynnJPanel;
    private JFrame frame;

    public void createJFrame() {
        //create JFrame Instance
        frame = new JFrame();
        frame.setTitle("ElwynnJFrame");
        frame.setLayout(new BorderLayout());

        frame.setSize(new Dimension(Parameters.getInstance().WINDOW_WIDTH, Parameters.getInstance().WINDOW_HEIGHT));
        frame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        frame.setVisible(true);
        //this will make the app to always display at the center
        frame.setLocationRelativeTo(null);
    }

    public void createJPanel() {
        elwynnJPanel = new ElwynnJPanel(Parameters.getInstance().WINDOW_WIDTH, Parameters.getInstance().WINDOW_HEIGHT);
        elwynnJPanel.setVisible(true);
        elwynnJPanel.addMyKeyListener();
    }

    public void addJPanelToJFrame() {
        frame.add(elwynnJPanel);
    }

    public void updateFrame() {
        elwynnJPanel.repaint();
    }
}
