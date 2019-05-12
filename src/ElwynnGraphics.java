import javax.swing.*;
import java.awt.*;

public class GUI {
    private JPanel jPanel;
    private JFrame frame;
    private int width = 1280;
    private int height = 720;

    void createJFrame() {
        //create JFrame Instance
        frame = new JFrame();
        frame.setTitle("JFrame Hello World!");
        frame.setLayout(new BorderLayout());

        frame.setSize(new Dimension(width, height));
        frame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        frame.setVisible(true);
        //this will make the app to always display at the center
        frame.setLocationRelativeTo(null);
    }

    void createJPanel() {
        jPanel = new JPanel();
        jPanel.setVisible(true);

    }

    public void setWidth(int width) {
        this.width = width;
    }

    public int getWidth() {
        return width;
    }

    public void setHeight(int height) {
        this.height = height;
    }

    public int getHeight() {
        return height;
    }
}
