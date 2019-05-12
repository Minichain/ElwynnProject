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

    public enum Facing {
        LEFT, RIGHT;
    }

    private Status characterStatus;
    private Facing characterFacing;

    private BufferedImage spriteSheet;
    private BufferedImage sprite;
    private double spriteFrame;
    private int spriteWidth;
    private int spriteHeight;
    private int idleFrames = 8;
    private int runningFrames = 6;
    private int jumpingFrames = 6;
    private float scale = 2;

    private Character() {
        xPosition = Parameters.getInstance().WINDOW_WIDTH / 2;
        yPosition = Parameters.getInstance().WINDOW_HEIGHT / 2;
        speed = 0.25;
        characterStatus = Status.IDLE;
        characterFacing = Facing.RIGHT;

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
        int animation = 0;

        switch (characterStatus) {
            case IDLE:
                if (characterFacing == Facing.RIGHT) {
                    animation= 0;
                } else if (characterFacing == Facing.LEFT) {
                    animation= 1;
                }
                break;
            case RUNNING:
                if (characterFacing == Facing.RIGHT) {
                    animation= 2;
                } else if (characterFacing == Facing.LEFT) {
                    animation= 3;
                }
                break;
            case JUMPING:
                if (characterFacing == Facing.RIGHT) {
                    animation= 4;
                } else if (characterFacing == Facing.LEFT) {
                    animation= 5;
                }
                break;
        }
        sprite = spriteSheet.getSubimage((int)spriteFrame * spriteWidth, animation * spriteHeight, spriteWidth - 1, spriteHeight - 1);
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
        if (characterStatus != Status.JUMPING) {
            characterStatus = Status.IDLE;
        }

        if (MyKeyListener.getInstance().iswKeyPressed()) {
            if (characterStatus != Status.JUMPING) {
                characterStatus = Status.RUNNING;
            }
            yPosition = yPosition - timeElapsed * speed;
        }
        if (MyKeyListener.getInstance().isaKeyPressed()) {
            if (characterStatus != Status.JUMPING) {
                characterStatus = Status.RUNNING;
            }
            characterFacing = Facing.LEFT;
            xPosition = xPosition - timeElapsed * speed;
        }
        if (MyKeyListener.getInstance().issKeyPressed()) {
            if (characterStatus != Status.JUMPING) {
                characterStatus = Status.RUNNING;
            }
            yPosition = yPosition + timeElapsed * speed;
        }
        if (MyKeyListener.getInstance().isdKeyPressed()) {
            if (characterStatus != Status.JUMPING) {
                characterStatus = Status.RUNNING;
            }
            characterFacing = Facing.RIGHT;
            xPosition = xPosition + timeElapsed * speed;
        }

        switch(characterStatus) {
            case IDLE:
                spriteFrame = (spriteFrame + (timeElapsed * 0.01)) % idleFrames;
                break;
            case RUNNING:
                spriteFrame = (spriteFrame + (timeElapsed * 0.01)) % runningFrames;
                break;
            case JUMPING:
                if (spriteFrame >= (jumpingFrames - 1)) {
                    characterStatus = Status.IDLE;
                } else {
                    spriteFrame = (spriteFrame + (timeElapsed * 0.01)) % jumpingFrames;
                }
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

    public Status getCharacterStatus() {
        return characterStatus;
    }

    public void performJump() {
        spriteFrame = 0;
        characterStatus = Status.JUMPING;
    }
}
