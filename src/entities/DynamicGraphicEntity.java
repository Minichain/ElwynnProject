package entities;

import main.OpenGLManager;
import scene.Camera;

public abstract class DynamicGraphicEntity extends GraphicEntity {
    public double speed;
    public double[] movementVector;
    public double[] movementVectorNormalized;

    public DynamicGraphicEntity(double x, double y) {
        super(x, y);
        movementVector = new double[]{0, 0};
        movementVectorNormalized = new double[]{0, 0};
    }

    @Override
    public void drawHitBox() {
        int width = (int) (getSprite().SPRITE_WIDTH * Camera.getZoom());
        int height = (int) ((-1) * getSprite().SPRITE_HEIGHT * Camera.getZoom());
//        OpenGLManager.drawRectangleOutline((float) getCameraCoordinates().x, (float) getCameraCoordinates().y, width, height);
        OpenGLManager.drawLine((float) getCameraCoordinates().x, (float) getCameraCoordinates().y, width, 0, 1,0f, 0f, 1f);
        OpenGLManager.drawLine((float) getCameraCoordinates().x, (float) getCameraCoordinates().y, 0, height, 1,0f, 1f, 0f);
        OpenGLManager.drawRectangleOutline((float) getCenterOfMassCameraCoordinates().x, (float) getCenterOfMassCameraCoordinates().y,
                1, 1, 1, 1f, 0f, 0f);
    }
}
