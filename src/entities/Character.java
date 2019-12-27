package entities;

import listeners.MyInputListener;
import main.Coordinates;
import main.Parameters;
import main.Texture;
import main.Utils;

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
    public static int spriteWidth;
    public static int spriteHeight;
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
        speed = 0.075;
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
        String path;
        path = "res/sprites/characters/link.png";
        texture = Texture.loadTexture(path);
        spriteWidth = 16;
        spriteHeight = 26;
        idleFrames = 1;
        runningFrames = 8;
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

    public void drawSprite() {
        float x = (float) getCurrentCoordinates().x;
        float y = (float) getCurrentCoordinates().y;
        float spriteWidth = 16 * 3;
        float spriteHeight = 26 * 3;

        //usually glOrtho would not be included in our game loop
        //however, since it's deprecated, let's keep it inside of this debug function which we will remove later
        glMatrixMode(GL_PROJECTION);
        glLoadIdentity();
        glOrtho(0, Parameters.getInstance().getWindowWidth(), Parameters.getInstance().getWindowHeight(), 0, 1, -1);
        glMatrixMode(GL_MODELVIEW);
        glLoadIdentity();
        glEnable(GL_TEXTURE_2D); //likely redundant; will be removed upon migration to "modern GL"

        //bind the texture before rendering it
        texture.bind();

        //setup our texture coordinates
        //(u,v) is another common way of writing (s,t)

        int xFrames = 8;
        int yFrames = 8;
        float u = ((1f / xFrames) * (int) spriteX);
        float v = 1f - ((1f / yFrames) * spriteY);
        float u2 = ((1f / xFrames) * (int) spriteX) + (1f / xFrames);
        float v2 = 1f - ((1f / yFrames) * spriteY) - (1f / yFrames);

        //immediate mode is deprecated -- we are only using it for quick debugging
        glColor4f(1f, 1f, 1f, 1f);
        glBegin(GL_QUADS);
        glTexCoord2f(u, v);
        glVertex2f(x, y);
        glTexCoord2f(u, v2);
        glVertex2f(x, y + spriteHeight);
        glTexCoord2f(u2, v2);
        glVertex2f(x + spriteWidth, y + spriteHeight);
        glTexCoord2f(u2, v);
        glVertex2f(x + spriteWidth, y);
        glEnd();
    }

    public void update(long timeElapsed) {
        getPreviousCoordinates().x = getCurrentCoordinates().x;
        getPreviousCoordinates().y = getCurrentCoordinates().y;
        if (characterStatus != Status.JUMPING) {
            characterStatus = Status.IDLE;
        }

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

//        if (!checkCollision((int)(getCurrentCoordinates().x + movement[0]), (int)(getCurrentCoordinates().y + movement[1]))
//                && !checkCollisionWithEntities((int)(getCurrentCoordinates().x + movement[0]), (int)(getCurrentCoordinates().y + movement[1]))) {
            getCurrentCoordinates().x = getCurrentCoordinates().x + movement[0];
            getCurrentCoordinates().y = getCurrentCoordinates().y + movement[1];
//        }

        displacementVector[0] = getCurrentCoordinates().x - getPreviousCoordinates().x;
        displacementVector[1] = getCurrentCoordinates().y - getPreviousCoordinates().y;

        if (isRunning()) {
            System.out.println("Character is running!!");
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
/*
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
*/
    public boolean checkCollision(int x, int y) {
        //TODO
        return false;
    }

    public boolean isRunning() {
        return (displacementVector[0] != 0 || displacementVector[1] != 0);
    }

    public Coordinates getCurrentCoordinates() {
        return getCoordinates();
    }

    public Status getCharacterStatus() {
        return characterStatus;
    }
}
