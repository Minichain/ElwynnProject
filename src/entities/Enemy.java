package entities;

import main.MyOpenGL;
import main.Texture;
import main.Utils;

import static org.lwjgl.opengl.GL11.*;

public class Enemy extends DynamicEntity{
    private Utils.DirectionFacing directionFacing;
    private Status status;

    public enum Status {
        IDLE, RUNNING, JUMPING, DYING, DEAD;
    }

    public Enemy(int x, int y) {
        super(x, y, x, y);
        initEnemy(x, y);
        loadSprite();
    }

    private void initEnemy(int x, int y) {
        getCurrentCoordinates().x = x;
        getCurrentCoordinates().y = y;
        HEALTH = 100f;
        SPEED = 0.15;
        status = Status.IDLE;
        directionFacing = Utils.DirectionFacing.DOWN;
        Scene.getInstance().getListOfEntities().add(this);
    }

    private void loadSprite() {
        String path = "res/sprites/characters/enemy.png";
        setSpriteSheet(Texture.loadTexture(path));
        SPRITE_WIDTH = 32;
        SPRITE_HEIGHT = 32;
        IDLE_FRAMES = 1;
        RUNNING_FRAMES = 8;
        DYING_FRAMES = 1;
        DEAD_FRAMES = 1;
        X_SPRITES = 8;
        Y_SPRITES = 10;
    }

    public void update(long timeElapsed) {
        getPreviousCoordinates().x = getCurrentCoordinates().x;
        getPreviousCoordinates().y = getCurrentCoordinates().y;
        if (status != Status.DYING && status != Status.DEAD)  {
            status = Status.IDLE;
        }

        HEALTH -= Math.random() / 2;    //FIXME (Remove later) Enemies receive damage over time in order to test death animation.
        if (HEALTH <= 0 && status != Status.DEAD) {
            status = Status.DYING;
        }

        double[] movement = new double[2];
        movement[0] = (Character.getInstance().getCurrentCoordinates().x - getCurrentCoordinates().x);
        movement[1] = (Character.getInstance().getCurrentCoordinates().y - getCurrentCoordinates().y);

        boolean chasing = (status != Status.DYING && status != Status.DEAD && Utils.module(movement) > 50 && Utils.module(movement) < 500);

        movement = Utils.normalizeVector(movement);

        //Normalize movement. The module of the movement vector must stay close to 1.
        if (movement[0] != 0 && movement[1] != 0) {
            movement[0] *= 0.75;
            movement[1] *= 0.75;
        }

        if (chasing) {
            getCurrentCoordinates().x = getCurrentCoordinates().x + (movement[0] * (timeElapsed * SPEED));
            getCurrentCoordinates().y = getCurrentCoordinates().y + (movement[1] * (timeElapsed * SPEED));
        }

        DISPLACEMENT_VECTOR = new double[]{getCurrentCoordinates().x - getPreviousCoordinates().x, getCurrentCoordinates().y - getPreviousCoordinates().y};

        if (DISPLACEMENT_VECTOR[0] != 0 || DISPLACEMENT_VECTOR[1] != 0) { //If character is moving
            directionFacing = Utils.checkDirectionFacing(DISPLACEMENT_VECTOR);
            status = Status.RUNNING;
        }

        switch (status) {
            case IDLE:
                setSpriteCoordinateFromSpriteSheetX((getSpriteCoordinateFromSpriteSheetX() + (timeElapsed * 0.01)) % IDLE_FRAMES);
                break;
            case RUNNING:
                setSpriteCoordinateFromSpriteSheetX((getSpriteCoordinateFromSpriteSheetX() + (timeElapsed * 0.01)) % RUNNING_FRAMES);
                break;
            case JUMPING:
                break;
            case DYING:
                double frame = (getSpriteCoordinateFromSpriteSheetX() + (timeElapsed * 0.01));
                if (frame > 1) {
                    status = Status.DEAD;
                } else {
                    setSpriteCoordinateFromSpriteSheetX(frame % DYING_FRAMES);
                }
                break;
            case DEAD:
                setSpriteCoordinateFromSpriteSheetX((getSpriteCoordinateFromSpriteSheetX() + (timeElapsed * 0.01)) % DEAD_FRAMES);
                break;
        }

        updateSpriteCoordinatesToDraw();
    }

    public void updateSpriteCoordinatesToDraw() {
        switch (status) {
            default:
            case IDLE:
                if (directionFacing == Utils.DirectionFacing.DOWN) {
                    setSpriteCoordinateFromSpriteSheetY(0);
                } else if (directionFacing == Utils.DirectionFacing.LEFT) {
                    setSpriteCoordinateFromSpriteSheetY(3);
                } else if (directionFacing == Utils.DirectionFacing.RIGHT) {
                    setSpriteCoordinateFromSpriteSheetY(1);
                } else {
                    setSpriteCoordinateFromSpriteSheetY(2);
                }
                break;
            case RUNNING:
                if (directionFacing == Utils.DirectionFacing.DOWN) {
                    setSpriteCoordinateFromSpriteSheetY(4);
                } else if (directionFacing == Utils.DirectionFacing.LEFT) {
                    setSpriteCoordinateFromSpriteSheetY(7);
                } else if (directionFacing == Utils.DirectionFacing.RIGHT) {
                    setSpriteCoordinateFromSpriteSheetY(5);
                } else {
                    setSpriteCoordinateFromSpriteSheetY(6);
                }
                break;
            case DYING:
                setSpriteCoordinateFromSpriteSheetY(8);
                break;
            case DEAD:
                setSpriteCoordinateFromSpriteSheetY(9);
                break;
        }
    }

    @Override
    public void drawSprite(int x, int y) {
        getSpriteSheet().bind();

        float u = ((1f / X_SPRITES) * (int) getSpriteCoordinateFromSpriteSheetX());
        float v = 1f - ((1f / Y_SPRITES) * (int) getSpriteCoordinateFromSpriteSheetY());
        float u2 = u + (1f / X_SPRITES);
        float v2 = v - (1f / Y_SPRITES);
        double scale = Scene.getZoom();

        glBegin(GL_QUADS);
        x -= (SPRITE_WIDTH / 2) * (int) scale;
        y -= (SPRITE_HEIGHT / 2) * (int) scale;
        MyOpenGL.drawTexture(x, y , u, v, u2, v2, (int) (SPRITE_WIDTH * scale), (int) (SPRITE_HEIGHT * scale));
        glEnd();
    }
}
