import javax.imageio.ImageIO;
import java.awt.image.BufferedImage;
import java.io.File;
import java.io.IOException;

public class Character {
    private static Character instance = null;
    private double xPosition;
    private double yPosition;
    private double speed;

    public enum Status {
        IDLE, RUNNING, JUMPING, DYING;
    }

    private Status characterStatus;

    private BufferedImage spriteSheet;
    private BufferedImage sprite;
    private double spriteFrame;
    private int spriteWidth;
    private int spriteHeight;
    private int idleFrames = 8;
    private int runningFrames = 6;
    private float scale = 2;

    private Character() {
        xPosition = Parameters.getInstance().WINDOW_WIDTH / 2;
        yPosition = Parameters.getInstance().WINDOW_HEIGHT / 2;
        speed = 0.1;
        characterStatus = Status.IDLE;

        try {
            loadSprite();
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    public static Character getInstance() {
        if (instance == null) {
            instance = new Character();
        }
        return instance;
    }

    private void loadSprite() throws IOException {
        String path = "res/Sprites/WolfSprites/80x48Wolf_FullSheet.png";
        spriteSheet = ImageIO.read(new File(path));
        spriteWidth = 80;
        spriteHeight = 48;
    }

    public BufferedImage getSprite() {
        switch (characterStatus) {
            case IDLE:
                sprite = spriteSheet.getSubimage((int)spriteFrame * spriteWidth, 0, spriteWidth - 1, spriteHeight - 1);
                break;
            case RUNNING:
                int animation = 1;
                sprite = spriteSheet.getSubimage((int)spriteFrame * spriteWidth, animation * spriteHeight, spriteWidth - 1, spriteHeight - 1);
                break;
        }
        return sprite;
    }

    public int getSpriteWidth() {
        return spriteWidth;
    }

    public int getSpriteHeight() {
        return spriteHeight;
    }

    public float getScale() {
        return scale;
    }

    public void updateCharacter(long timeElapsed) {
        characterStatus = Status.IDLE;

        if (MyKeyListener.getInstance().iswKeyPressed()) {
            characterStatus = Status.RUNNING;
            yPosition = yPosition - timeElapsed * speed;
        }
        if (MyKeyListener.getInstance().isaKeyPressed()) {
            characterStatus = Status.RUNNING;
            xPosition = xPosition - timeElapsed * speed;
        }
        if (MyKeyListener.getInstance().issKeyPressed()) {
            characterStatus = Status.RUNNING;
            yPosition = yPosition + timeElapsed * speed;
        }
        if (MyKeyListener.getInstance().isdKeyPressed()) {
            characterStatus = Status.RUNNING;
            xPosition = xPosition + timeElapsed * speed;
        }

        switch(characterStatus) {
            case IDLE:
                spriteFrame = (spriteFrame + (timeElapsed * 0.01)) % idleFrames;
                break;
            case RUNNING:
                spriteFrame = (spriteFrame + (timeElapsed * 0.01)) % runningFrames;
                break;
        }
    }

    public void setXPosition(double x) {
        xPosition = x;
    }

    public double getXPosition() {
        return xPosition;
    }

    public void setYPosition(double y) {
        yPosition = y;
    }

    public double getYPosition() {
        return yPosition;
    }
}
