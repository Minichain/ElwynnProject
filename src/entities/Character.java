package entities;

import listeners.MyKeyListener;

import javax.imageio.ImageIO;
import java.awt.image.BufferedImage;
import java.io.File;
import java.io.IOException;

import main.Coordinates;
import main.Utils;

public class Character {
    private static Character instance = null;
    private static Coordinates currentCoordinates;
    private static Coordinates previousCoordinates;
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
        currentCoordinates = new Coordinates(Scene.getInstance().getSpriteWidth() / 2, Scene.getInstance().getSpriteHeight() / 2);
        previousCoordinates = new Coordinates(Scene.getInstance().getSpriteWidth() / 2, Scene.getInstance().getSpriteHeight() / 2);
        speed = 0.25;
        characterStatus = Status.IDLE;
        characterFacing = Utils.DirectionFacing.RIGHT;
        displacementVector = new double[2];

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
//        path = "res/sprites/characters/80x48Wolf_FullSheet.png";
        path = "res/sprites/characters/51x72bardo_character_01.png";
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
        previousCoordinates.setxCoordinate(currentCoordinates.getxCoordinate());
        previousCoordinates.setyCoordinate(currentCoordinates.getyCoordinate());
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

        if (!checkCollision((int)(currentCoordinates.getxCoordinate() + movement[0]), (int)(currentCoordinates.getyCoordinate() + movement[1]))) {
            currentCoordinates.setxCoordinate(currentCoordinates.getxCoordinate() + movement[0]);
            currentCoordinates.setyCoordinate(currentCoordinates.getyCoordinate() + movement[1]);
        }

        displacementVector[0] = currentCoordinates.getxCoordinate() - previousCoordinates.getxCoordinate();
        displacementVector[1] = currentCoordinates.getyCoordinate() - previousCoordinates.getyCoordinate();

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

    public boolean checkCollision(int x, int y) {
        int pixelValue = Scene.getInstance().getCollisionsMap().getRGB(x, y);
        return pixelValue != 0;
    }

    public boolean isRunning() {
        return (displacementVector[0] != 0 || displacementVector[1] != 0);
    }

    public Coordinates getCurrentCoordinates() {
        return currentCoordinates;
    }

    public void setCoordinates(int x, int y) {
        currentCoordinates.setxCoordinate(x);
        currentCoordinates.setyCoordinate(y);
    }

    public Status getCharacterStatus() {
        return characterStatus;
    }

    public void performJump() {
        spriteFrame = 0;
        characterStatus = Status.JUMPING;
    }
}
