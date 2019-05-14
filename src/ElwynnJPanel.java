import javax.swing.*;
import java.awt.*;

public class ElwynnJPanel extends JPanel {

    ElwynnJPanel(int width, int height) {
        setPreferredSize(new Dimension(width, height));
    }

    public void addMyKeyListener() {
        addKeyListener(MyKeyListener.getInstance());
        setFocusable(true);
    }

    public void paint(Graphics g){
        super.paint(g); // cleans the panel

        g.drawImage(Scene.getInstance().getSprite(),
                0 - Camera.getInstance().getxCoordinate() + (Parameters.getInstance().getWindowWidth() / 2),
                0 - Camera.getInstance().getyCoordinate() + (Parameters.getInstance().getWindowHeight() / 2),
                (int)(Scene.getInstance().getSpriteWidth() * Scene.getInstance().getScale()),
                (int)(Scene.getInstance().getSpriteHeight() * Scene.getInstance().getScale()),
                null);

        g.drawImage(Character.getInstance().getSprite(),
                (int)Character.getInstance().getxCoordinate()
                        - Camera.getInstance().getxCoordinate()
                        + (Parameters.getInstance().getWindowWidth() / 2)
                        - (Character.getInstance().getSpriteWidth() / 2),
                (int)Character.getInstance().getyCoordinate()
                        - Camera.getInstance().getyCoordinate()
                        + (Parameters.getInstance().getWindowHeight() / 2)
                        - (Character.getInstance().getSpriteHeight() / 2),
                (int)(Character.getInstance().getSpriteWidth() * Character.getInstance().getScale()),
                (int)(Character.getInstance().getSpriteHeight() * Character.getInstance().getScale()),
                null);

        g.drawString("X: " + Character.getInstance().getxCoordinate(), 10, 20);
        g.drawString("Y: " + Character.getInstance().getyCoordinate(), 10, 35);
    }
}