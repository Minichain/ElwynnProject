package entities;

import listeners.MyInputListener;
import main.*;

import static org.lwjgl.opengl.GL11.*;

public class Character extends DynamicEntity {
    private static Character instance = null;

    public enum Status {
        IDLE, RUNNING, JUMPING, DYING;
    }

    private static Status characterStatus;
    private static Utils.DirectionFacing directionFacing;


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
        setSpeed(0.25);
        characterStatus = Status.IDLE;
        directionFacing = Utils.DirectionFacing.DOWN;
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
        setTexture(Texture.loadTexture(path));
        setSpriteWidth(16);
        setSpriteHeight(26);
        setIdleFrames(1);
        setRunningFrames(8);
        setHorizontalSprites(8);
        setVerticalSprites(8);
    }

    public void updateSpriteCoordinatesToDraw() {
        switch (characterStatus) {
            default:
            case IDLE:
                if (directionFacing == Utils.DirectionFacing.DOWN) {
                    setSpriteCoordinateFromTileSheetY(0);
                } else if (directionFacing == Utils.DirectionFacing.LEFT) {
                    setSpriteCoordinateFromTileSheetY(3);
                } else if (directionFacing == Utils.DirectionFacing.RIGHT) {
                    setSpriteCoordinateFromTileSheetY(1);
                } else {
                    setSpriteCoordinateFromTileSheetY(2);
                }
                break;
            case RUNNING:
                if (directionFacing == Utils.DirectionFacing.DOWN) {
                    setSpriteCoordinateFromTileSheetY(4);
                } else if (directionFacing == Utils.DirectionFacing.LEFT) {
                    setSpriteCoordinateFromTileSheetY(7);
                } else if (directionFacing == Utils.DirectionFacing.RIGHT) {
                    setSpriteCoordinateFromTileSheetY(5);
                } else {
                    setSpriteCoordinateFromTileSheetY(6);
                }
                break;
            case JUMPING:
                break;
        }
    }

    @Override
    public void drawSprite(int x, int y) {
        getTexture().bind();

        float u = ((1f / getHorizontalSprites()) * (int) getSpriteCoordinateFromTileSheetX());
        float v = 1f - ((1f / getVerticalSprites()) * (int) getSpriteCoordinateFromTileSheetY());
        float u2 = u + (1f / getHorizontalSprites());
        float v2 = v - (1f / getVerticalSprites());
        double scale = Scene.getZoom();

        glBegin(GL_QUADS);
        x -= (getSpriteWidth() / 2) * (int) scale;
        y -= (getSpriteHeight() / 2) * (int) scale;
        MyOpenGL.drawTexture(x, y , u, v, u2, v2, (int) (getSpriteWidth() * scale), (int) (getSpriteHeight() * scale));
        glEnd();
    }

    public void update(long timeElapsed) {
        getPreviousCoordinates().x = getCurrentCoordinates().x;
        getPreviousCoordinates().y = getCurrentCoordinates().y;
        characterStatus = Status.IDLE;

        double[] movement = new double[2];
        if (MyInputListener.sKeyPressed) {
            movement[1] = movement[1] + timeElapsed * getSpeed();
        }
        if (MyInputListener.aKeyPressed) {
            movement[0] = movement[0] - timeElapsed * getSpeed();
        }
        if (MyInputListener.wKeyPressed) {
            movement[1] = movement[1] - timeElapsed * getSpeed();
        }
        if (MyInputListener.dKeyPressed) {
            movement[0] = movement[0] + timeElapsed * getSpeed();
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

        setDisplacementVector(new double[]{getCurrentCoordinates().x - getPreviousCoordinates().x, getCurrentCoordinates().y - getPreviousCoordinates().y});

        if (getDisplacementVector()[0] != 0 || getDisplacementVector()[1] != 0) { //If character is moving
            directionFacing = Utils.checkDirectionFacing(getDisplacementVector());
            characterStatus = Status.RUNNING;
        }

        switch(characterStatus) {
            case IDLE:
                setSpriteCoordinateFromTileSheetX((getSpriteCoordinateFromTileSheetX() + (timeElapsed * 0.01)) % getIdleFrames());
                break;
            case RUNNING:
                setSpriteCoordinateFromTileSheetX((getSpriteCoordinateFromTileSheetX() + (timeElapsed * 0.01)) % getRunningFrames());
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
}
