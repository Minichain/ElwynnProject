package entities;

import listeners.MyKeyListener;

import javax.imageio.ImageIO;
import java.awt.image.BufferedImage;
import java.io.File;
import java.io.IOException;
import java.util.List;

import main.Coordinates;
import main.Utils;

public class Character extends DynamicEntity {
    private static Character instance = null;
    private static double speed;
    private static double[] displacementVector;

    public enum Status {
        IDLE, RUNNING, JUMPING, DYING;
    }

    private static Status characterStatus;
    private static Utils.DirectionFacing characterFacing;

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
        super(Scene.getInstance().getSpriteWidth() / 2, Scene.getInstance().getSpriteHeight() / 2,
                Scene.getInstance().getSpriteWidth() / 2, Scene.getInstance().getSpriteHeight() / 2);
        initCharacter();
        try {
            loadSprite();
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    public void resetCharacter() {
        initCharacter();
    }

    private void initCharacter() {
        getCurrentCoordinates().setxCoordinate(Scene.getInstance().getSpriteWidth() / 2);
        getCurrentCoordinates().setyCoordinate(Scene.getInstance().getSpriteHeight() / 2);
        speed = 0.25;
        characterStatus = Status.IDLE;
        characterFacing = Utils.DirectionFacing.RIGHT;
        displacementVector = new double[2];
    }

    public static Character getInstance() {
        if (instance == null) {
            instance = new Character();
            Scene.getInstance().getListOfEntities().add(instance);
        }
        return instance;
    }

    private void loadSprite() throws IOException {
        String path;
//        path = "res/sprites/characters/80x48Wolf_FullSheet.png";
        path = "res/sprites/characters/51x72bardo_character_01.png";
        spriteSheet = ImageIO.read(new File(path));

        //Bard
        spriteWidth = 64;
        spriteHeight = 90;
        scale = 1.00f;
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
                if (characterFacing == Utils.DirectionFacing.DOWN) {
                    animation= 4;
                } else if (characterFacing == Utils.DirectionFacing.LEFT) {
                    animation= 5;
                } else if (characterFacing == Utils.DirectionFacing.RIGHT) {
                    animation= 6;
                } else {
                    animation= 7;
                }
                break;
            case RUNNING:
                if (characterFacing == Utils.DirectionFacing.DOWN) {
                    animation= 0;
                } else if (characterFacing == Utils.DirectionFacing.LEFT) {
                    animation= 1;
                } else if (characterFacing == Utils.DirectionFacing.RIGHT) {
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
        getPreviousCoordinates().setxCoordinate(getCurrentCoordinates().getxCoordinate());
        getPreviousCoordinates().setyCoordinate(getCurrentCoordinates().getyCoordinate());
        if (characterStatus != Status.JUMPING) {
            characterStatus = Status.IDLE;
        }

        double[] movement = new double[2];
        if (MyKeyListener.getInstance().iswKeyPressed()) {
            movement[1] = movement[1] - timeElapsed * speed;
        }
        if (MyKeyListener.getInstance().isaKeyPressed()) {
            movement[0] = movement[0] - timeElapsed * speed;
        }
        if (MyKeyListener.getInstance().issKeyPressed()) {
            movement[1] = movement[1] + timeElapsed * speed;
        }
        if (MyKeyListener.getInstance().isdKeyPressed()) {
            movement[0] = movement[0] + timeElapsed * speed;
        }

        //Normalize movement. The module of the movement vector must stay close to 1.
        if (movement[0] != 0 && movement[1] != 0) {
            movement[0] *= 0.75;
            movement[1] *= 0.75;
        }

        if (!checkCollision((int)(getCurrentCoordinates().getxCoordinate() + movement[0]), (int)(getCurrentCoordinates().getyCoordinate() + movement[1]))
                && !checkCollisionWithEntities((int)(getCurrentCoordinates().getxCoordinate() + movement[0]), (int)(getCurrentCoordinates().getyCoordinate() + movement[1]))) {
            getCurrentCoordinates().setxCoordinate(getCurrentCoordinates().getxCoordinate() + movement[0]);
            getCurrentCoordinates().setyCoordinate(getCurrentCoordinates().getyCoordinate() + movement[1]);
        }

        displacementVector[0] = getCurrentCoordinates().getxCoordinate() - getPreviousCoordinates().getxCoordinate();
        displacementVector[1] = getCurrentCoordinates().getyCoordinate() - getPreviousCoordinates().getyCoordinate();

        if (isRunning() && characterStatus != Status.JUMPING) {
            characterFacing = Utils.checkDirectionFacing(displacementVector);
            characterStatus = Status.RUNNING;
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

    private boolean checkCollisionWithEntities(int x, int y) {
        List<Entity> listOfEntities = Scene.getInstance().getListOfEntities();
        double distanceToEntity;
        for (int i = 0; i < listOfEntities.size(); i++) {
            if (listOfEntities.get(i) != this) {    //Do not check collision with yourself!
                distanceToEntity = Utils.module(listOfEntities.get(i).getCoordinates(), new Coordinates(x, y));
                if (distanceToEntity < 50) {
                    return true;
                }
            }
        }
        return false;
    }

    public boolean checkCollision(int x, int y) {
        if (Scene.getInstance().getCollisionsMap() == null) {
            return false;
        }
        int pixelValue = Scene.getInstance().getCollisionsMap().getRGB(x, y);
        return pixelValue != 0;
    }

    public boolean isRunning() {
        return (displacementVector[0] != 0 || displacementVector[1] != 0);
    }

    public Coordinates getCurrentCoordinates() {
        return getCoordinates();
    }

    public void setCoordinates(int x, int y) {
        getCurrentCoordinates().setxCoordinate(x);
        getCurrentCoordinates().setyCoordinate(y);
    }

    public Status getCharacterStatus() {
        return characterStatus;
    }

    public void performJump() {
        spriteFrame = 0;
        characterStatus = Status.JUMPING;
    }
}
