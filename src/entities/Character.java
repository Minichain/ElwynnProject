package entities;

import listeners.MyInputListener;
import main.*;

import static org.lwjgl.opengl.GL11.*;

public class Character extends DynamicEntity {
    private static Character instance = null;
    private static double speed;
    private static double[] displacementVector;

    public enum Status {
        IDLE, RUNNING, JUMPING, DYING;
    }

    private static Status characterStatus;
    private static Utils.DirectionFacing characterFacing;

    private static Texture texture;
    private static double spriteX;
    private static int spriteY;
    private static int horizontalSprites;
    private static int verticalSprites;
    private static int spriteWidth;
    private static int spriteHeight;
    private static int idleFrames;
    private static int runningFrames;

    private Character() {
        super((int) Parameters.getInstance().getStartingCoordinates().x,
                (int) Parameters.getInstance().getStartingCoordinates().y,
                (int) Parameters.getInstance().getStartingCoordinates().x,
                (int) Parameters.getInstance().getStartingCoordinates().y);
        initCharacter();
        loadSprite();
    }

    public void resetCharacter() {
        initCharacter();
    }

    private void initCharacter() {
        getCurrentCoordinates().x = Parameters.getInstance().getStartingCoordinates().x;
        getCurrentCoordinates().y = Parameters.getInstance().getStartingCoordinates().y;
        speed = 0.25;
        characterStatus = Status.IDLE;
        characterFacing = Utils.DirectionFacing.DOWN;
        displacementVector = new double[2];
    }

    public static Character getInstance() {
        if (instance == null) {
            instance = new Character();
            Scene.getInstance().getListOfEntities().add(instance);
        }
        return instance;
    }

    private void loadSprite() {
        String path = "res/sprites/characters/link.png";
        texture = Texture.loadTexture(path);
        spriteWidth = 16;
        spriteHeight = 26;
        idleFrames = 1;
        runningFrames = 8;
        horizontalSprites = 8;
        verticalSprites = 8;
    }

    public void updateSpriteCoordinatesToDraw() {
        switch (characterStatus) {
            default:
            case IDLE:
                if (characterFacing == Utils.DirectionFacing.DOWN) {
                    spriteY = 0;
                } else if (characterFacing == Utils.DirectionFacing.LEFT) {
                    spriteY = 3;
                } else if (characterFacing == Utils.DirectionFacing.RIGHT) {
                    spriteY = 1;
                } else {
                    spriteY = 2;
                }
                break;
            case RUNNING:
                if (characterFacing == Utils.DirectionFacing.DOWN) {
                    spriteY = 4;
                } else if (characterFacing == Utils.DirectionFacing.LEFT) {
                    spriteY = 7;
                } else if (characterFacing == Utils.DirectionFacing.RIGHT) {
                    spriteY = 5;
                } else {
                    spriteY = 6;
                }
                break;
            case JUMPING:
                break;
        }
    }

    @Override
    public void drawSprite(int x, int y) {
        texture.bind();

        float u = ((1f / horizontalSprites) * (int) spriteX);
        float v = 1f - ((1f / verticalSprites) * spriteY);
        float u2 = u + (1f / horizontalSprites);
        float v2 = v - (1f / verticalSprites);
        double scale = Scene.getZoom();

        glBegin(GL_QUADS);
        MyOpenGL.drawTexture(x, y, u, v, u2, v2, (int) (spriteWidth * scale), (int) (spriteHeight * scale));
        glEnd();
    }

    public void update(long timeElapsed) {
        getPreviousCoordinates().x = getCurrentCoordinates().x;
        getPreviousCoordinates().y = getCurrentCoordinates().y;
        characterStatus = Status.IDLE;

        double[] movement = new double[2];
        if (MyInputListener.sKeyPressed) {
            movement[1] = movement[1] + timeElapsed * speed;
        }
        if (MyInputListener.aKeyPressed) {
            movement[0] = movement[0] - timeElapsed * speed;
        }
        if (MyInputListener.wKeyPressed) {
            movement[1] = movement[1] - timeElapsed * speed;
        }
        if (MyInputListener.dKeyPressed) {
            movement[0] = movement[0] + timeElapsed * speed;
        }

        //Normalize movement. The module of the movement vector must stay close to 1.
        if (movement[0] != 0 && movement[1] != 0) {
            movement[0] *= 0.75;
            movement[1] *= 0.75;
        }

        if (!checkCollision((int)(getCurrentCoordinates().x + movement[0]), (int)(getCurrentCoordinates().y + movement[1]))
                && !checkCollisionWithEntities((int)(getCurrentCoordinates().x + movement[0]), (int)(getCurrentCoordinates().y + movement[1]))) {
            getCurrentCoordinates().x = getCurrentCoordinates().x + movement[0];
            getCurrentCoordinates().y = getCurrentCoordinates().y + movement[1];
        }

        displacementVector[0] = getCurrentCoordinates().x - getPreviousCoordinates().x;
        displacementVector[1] = getCurrentCoordinates().y - getPreviousCoordinates().y;

        if (displacementVector[0] != 0 || displacementVector[1] != 0) { //If character is moving
            characterFacing = Utils.checkDirectionFacing(displacementVector);
            characterStatus = Status.RUNNING;
        }

        switch(characterStatus) {
            case IDLE:
                spriteX = (spriteX + (timeElapsed * 0.01)) % idleFrames;
                break;
            case RUNNING:
                spriteX = (spriteX + (timeElapsed * 0.01)) % runningFrames;
                break;
            case JUMPING:
                break;
        }

        updateSpriteCoordinatesToDraw();
    }

    private boolean checkCollisionWithEntities(int x, int y) {
//        List<Entity> listOfEntities = Scene.getInstance().getListOfEntities();
//        double distanceToEntity;
//        for (int i = 0; i < listOfEntities.size(); i++) {
//            if (listOfEntities.get(i) != this) {    //Do not check collision with yourself!
//                distanceToEntity = Utils.module(listOfEntities.get(i).getCoordinates(), new Coordinates(x, y));
//                if (distanceToEntity < 50) {
//                    return true;
//                }
//            }
//        }
        return false;
    }

    public boolean checkCollision(int x, int y) {
        //TODO
        return false;
    }

    public Status getCharacterStatus() {
        return characterStatus;
    }

    public Coordinates getCurrentCoordinates() {
        return getCoordinates();
    }
}
