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
//        g.fillRect((int)Character.getInstance().getXPosition(),
//                (int)Character.getInstance().getYPosition(),
//                40,
//                40);

        g.drawImage(Character.getInstance().getSprite(),
                (int)Character.getInstance().getXPosition(),
                (int)Character.getInstance().getYPosition(),
                (int)(Character.getInstance().getSpriteWidth() * Character.getInstance().getScale()),
                (int)(Character.getInstance().getSpriteHeight() * Character.getInstance().getScale()),
                this);
    }
}