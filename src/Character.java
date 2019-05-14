import javax.imageio.ImageIO;
import java.awt.image.BufferedImage;
import java.io.File;
import java.io.IOException;

public class Character {
    private static Character instance = null;
    private static double xCoordinate;
    private static double yCoordinate;
    private static double speed;

    public enum Status {
        IDLE, RUNNING, JUMPING, DYING;
    }

    public enum Facing {
        LEFT, RIGHT, UP, DOWN;
    }

    private static Status characterStatus;
    private static Facing characterFacing;

    private static BufferedImage spriteSheet;
    private static BufferedImage sprite;
    private static double spriteFrame;
    private static int spriteWidth;
    private static int spriteHeight;
    private static int idleFrames;
    private static int runningFrames;
    private static int specialAnimationFrames;
    private static float scale;

    private Character() {
        xCoordinate = Scene.getInstance().getSpriteWidth() / 2;
        yCoordinate = Scene.getInstance().getSpriteHeight() / 2;
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
        String path;
//        path = "res/Sprites/characters/80x48Wolf_FullSheet.png";
        path = "res/Sprites/characters/51x72bardo_character_01.png";
        spriteSheet = ImageIO.read(new File(path));

        //Bard
        spriteWidth = 51;
        spriteHeight = 72;
        scale = 1.25f;
        idleFrames = 1;
        runningFrames = 3;
        specialAnimationFrames = 4;

//        //Wolf
//        spriteWidth = 80;
//        spriteHeight = 48;
//        scale = 2;
//        idleFrames = 8;
//        runningFrames = 6;
//        specialAnimationFrames = 6;
    }

    public BufferedImage getSprite() {
        int animation;

        switch (characterStatus) {
            default:
            case IDLE:
                if (characterFacing == Facing.DOWN) {
                    animation= 4;
                } else if (characterFacing == Facing.LEFT) {
                    animation= 5;
                } else if (characterFacing == Facing.RIGHT) {
                    animation= 6;
                } else {
                    animation= 7;
                }
                break;
            case RUNNING:
                if (characterFacing == Facing.DOWN) {
                    animation= 0;
                } else if (characterFacing == Facing.LEFT) {
                    animation= 1;
                } else if (characterFacing == Facing.RIGHT) {
                    animation= 2;
                } else {
                    animation= 3;
                }
                break;
            case JUMPING:
                animation= 8;
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
            characterFacing = Facing.UP;
            yCoordinate = yCoordinate - timeElapsed * speed;
        }
        if (MyKeyListener.getInstance().isaKeyPressed()) {
            if (characterStatus != Status.JUMPING) {
                characterStatus = Status.RUNNING;
            }
            characterFacing = Facing.LEFT;
            xCoordinate = xCoordinate - timeElapsed * speed;
        }
        if (MyKeyListener.getInstance().issKeyPressed()) {
            if (characterStatus != Status.JUMPING) {
                characterStatus = Status.RUNNING;
            }
            characterFacing = Facing.DOWN;
            yCoordinate = yCoordinate + timeElapsed * speed;
        }
        if (MyKeyListener.getInstance().isdKeyPressed()) {
            if (characterStatus != Status.JUMPING) {
                characterStatus = Status.RUNNING;
            }
            characterFacing = Facing.RIGHT;
            xCoordinate = xCoordinate + timeElapsed * speed;
        }

        switch(characterStatus) {
            case IDLE:
                spriteFrame = (spriteFrame + (timeElapsed * 0.01)) % idleFrames;
                break;
            case RUNNING:
                spriteFrame = (spriteFrame + (timeElapsed * 0.01)) % runningFrames;
                break;
            case JUMPING:
                if (spriteFrame >= (specialAnimationFrames - 1)) {
                    characterStatus = Status.IDLE;
                } else {
                    spriteFrame = (spriteFrame + (timeElapsed * 0.01)) % specialAnimationFrames;
                }
                break;
        }
    }

    public double getxCoordinate() {
        return xCoordinate;
    }

    public double getyCoordinate() {
        return yCoordinate;
    }

    public void setCoordinates(int x, int y) {
        xCoordinate = x;
        yCoordinate = y;
    }

    public Status getCharacterStatus() {
        return characterStatus;
    }

    public void performJump() {
        spriteFrame = 0;
        characterStatus = Status.JUMPING;
    }
}
