package entities;

import main.MyOpenGL;
import main.Texture;
import main.Utils;

import static org.lwjgl.opengl.GL11.*;

public class Enemy extends DynamicEntity{
    private Utils.DirectionFacing directionFacing;
    private Status status;

    public enum Status {
        IDLE, RUNNING, JUMPING, DYING;
    }

    public Enemy(int x, int y) {
        super(x, y, x, y);
        initEnemy(x, y);
        loadSprite();
    }

    private void initEnemy(int x, int y) {
        getCurrentCoordinates().x = x;
        getCurrentCoordinates().y = y;
        setSpeed(0.15);
        status = Status.IDLE;
        directionFacing = Utils.DirectionFacing.DOWN;
        Scene.getInstance().getListOfEntities().add(this);
    }

    private void loadSprite() {
        String path = "res/sprites/characters/enemy.png";
        setTexture(Texture.loadTexture(path));
        setSpriteWidth(16);
        setSpriteHeight(26);
        setIdleFrames(1);
        setRunningFrames(8);
        setHorizontalSprites(8);
        setVerticalSprites(8);
    }

    public void update(long timeElapsed) {
        getPreviousCoordinates().x = getCurrentCoordinates().x;
        getPreviousCoordinates().y = getCurrentCoordinates().y;
        status = Status.IDLE;

        double[] movement = new double[2];
        movement[0] = (Character.getInstance().getCurrentCoordinates().x - getCurrentCoordinates().x);
        movement[1] = (Character.getInstance().getCurrentCoordinates().y - getCurrentCoordinates().y);

        boolean chasing;
        chasing = Utils.module(movement) > 50 && Utils.module(movement) < 500;

        movement = Utils.normalizeVector(movement);

//        //Normalize movement. The module of the movement vector must stay close to 1.
//        if (movement[0] != 0 && movement[1] != 0) {
//            movement[0] *= 0.75;
//            movement[1] *= 0.75;
//        }

        if (chasing) {
            getCurrentCoordinates().x = getCurrentCoordinates().x + (movement[0] * (timeElapsed * getSpeed()));
            getCurrentCoordinates().y = getCurrentCoordinates().y + (movement[1] * (timeElapsed * getSpeed()));
        }

        setDisplacementVector(new double[]{getCurrentCoordinates().x - getPreviousCoordinates().x, getCurrentCoordinates().y - getPreviousCoordinates().y});

        if (getDisplacementVector()[0] != 0 || getDisplacementVector()[1] != 0) { //If character is moving
            directionFacing = Utils.checkDirectionFacing(getDisplacementVector());
            status = Status.RUNNING;
        }

        switch(status) {
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

    public void updateSpriteCoordinatesToDraw() {
        switch (status) {
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
}
