package entities;

import main.Coordinates;
import main.Texture;

public abstract class DynamicEntity extends Entity {
    private Coordinates previousCoordinates;
    private double speed;
    private double[] displacementVector;
    private Texture texture;
    private double spriteCoordinateFromTileSheetX;
    private double spriteCoordinateFromTileSheetY;
    private int horizontalSprites;
    private int verticalSprites;
    private int spriteWidth;
    private int spriteHeight;
    private int idleFrames;
    private int runningFrames;

    public DynamicEntity(int x, int y, int prevX, int prevY) {
        super(x, y);
        previousCoordinates = new Coordinates(prevX, prevY);
        displacementVector = new double[2];
    }

    public Coordinates getCurrentCoordinates() {
        return getCoordinates();
    }

    public Coordinates getPreviousCoordinates() {
        return previousCoordinates;
    }

    public double getSpeed() {
        return speed;
    }

    public void setSpeed(double speed) {
        this.speed = speed;
    }

    public double[] getDisplacementVector() {
        return displacementVector;
    }

    public void setDisplacementVector(double[] displacementVector) {
        this.displacementVector = displacementVector;
    }

    public Texture getTexture() {
        return texture;
    }

    public void setTexture(Texture texture) {
        this.texture = texture;
    }

    public int getSpriteWidth() {
        return spriteWidth;
    }

    public void setSpriteWidth(int spriteWidth) {
        this.spriteWidth = spriteWidth;
    }

    public int getSpriteHeight() {
        return spriteHeight;
    }

    public void setSpriteHeight(int spriteHeight) {
        this.spriteHeight = spriteHeight;
    }

    public int getIdleFrames() {
        return idleFrames;
    }

    public void setIdleFrames(int idleFrames) {
        this.idleFrames = idleFrames;
    }

    public int getRunningFrames() {
        return runningFrames;
    }

    public void setRunningFrames(int runningFrames) {
        this.runningFrames = runningFrames;
    }

    public int getHorizontalSprites() {
        return horizontalSprites;
    }

    public void setHorizontalSprites(int horizontalSprites) {
        this.horizontalSprites = horizontalSprites;
    }

    public int getVerticalSprites() {
        return verticalSprites;
    }

    public void setVerticalSprites(int verticalSprites) {
        this.verticalSprites = verticalSprites;
    }

    public double getSpriteCoordinateFromTileSheetX() {
        return spriteCoordinateFromTileSheetX;
    }

    public void setSpriteCoordinateFromTileSheetX(double spriteCoordinateFromTileSheetX) {
        this.spriteCoordinateFromTileSheetX = spriteCoordinateFromTileSheetX;
    }

    public double getSpriteCoordinateFromTileSheetY() {
        return spriteCoordinateFromTileSheetY;
    }

    public void setSpriteCoordinateFromTileSheetY(double spriteCoordinateFromTileSheetY) {
        this.spriteCoordinateFromTileSheetY = spriteCoordinateFromTileSheetY;
    }
}
