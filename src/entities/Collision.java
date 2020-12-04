package entities;

import main.Coordinates;
import main.OpenGLManager;
import scene.Camera;
import utils.MathUtils;

import static org.lwjgl.opengl.GL11.*;

public class Collision {
    private Coordinates center;
    private double width;
    private double height;
    private double radius;
    private CollisionType collisionType;

    public enum CollisionType {
        SQUARE, CIRCLE
    }

    public Collision(Coordinates center, double width, double height) {
        this.center = center;
        this.width = width;
        this.height = height;
        this.collisionType = CollisionType.SQUARE;
    }

    public Collision(Coordinates center, double radius) {
        this.center = center;
        this.radius = radius;
        this.collisionType = CollisionType.CIRCLE;
    }

    public double getWidth() {
        return width;
    }

    public double getHeight() {
        return height;
    }

    public boolean isColliding(Coordinates coordinates) {
        switch (collisionType) {
            case SQUARE:
                return (Math.abs(coordinates.x - center.x) <= (width / 2)) && (Math.abs(coordinates.y - center.y) <= (height / 2));
            case CIRCLE:
                return MathUtils.module(coordinates, center) <= radius;
            default:
                return false;
        }
    }

    public void draw() {
        Coordinates centerCameraCoordinates = center.toCameraCoordinates();
        double widthRelativeToCamera = width * Camera.getZoom();
        double heightRelativeToCamera = height * Camera.getZoom();

        OpenGLManager.glBegin(GL_LINES);
        glColor4f(1f, 0f, 0f, 1f);

        switch (collisionType) {
            case SQUARE:
                //1st Line
                glVertex2i((int) (centerCameraCoordinates.x - widthRelativeToCamera / 2), (int) (centerCameraCoordinates.y - heightRelativeToCamera / 2));
                glVertex2i((int) (centerCameraCoordinates.x + widthRelativeToCamera / 2), (int) (centerCameraCoordinates.y - heightRelativeToCamera / 2));

                //2nd Line
                glVertex2i((int) (centerCameraCoordinates.x + widthRelativeToCamera / 2), (int) (centerCameraCoordinates.y - heightRelativeToCamera / 2));
                glVertex2i((int) (centerCameraCoordinates.x + widthRelativeToCamera / 2), (int) (centerCameraCoordinates.y + heightRelativeToCamera / 2));

                //3rd Line
                glVertex2i((int) (centerCameraCoordinates.x + widthRelativeToCamera / 2), (int) (centerCameraCoordinates.y + heightRelativeToCamera / 2));
                glVertex2i((int) (centerCameraCoordinates.x - widthRelativeToCamera / 2), (int) (centerCameraCoordinates.y + heightRelativeToCamera / 2));

                //4th Line
                glVertex2i((int) (centerCameraCoordinates.x - widthRelativeToCamera / 2), (int) (centerCameraCoordinates.y + heightRelativeToCamera / 2));
                glVertex2i((int) (centerCameraCoordinates.x - widthRelativeToCamera / 2), (int) (centerCameraCoordinates.y - heightRelativeToCamera / 2));

                break;
            case CIRCLE:
                double angle = 0;
                int numberOfVertices = 16;
                double angleStep = 2.0 * Math.PI / (double) numberOfVertices;
                for (int i = 0; i < numberOfVertices; i++) {
                    double radius = this.radius * Camera.getZoom();
                    double x1 = (Math.cos(angle) * radius) + centerCameraCoordinates.x;
                    double y1 = (Math.sin(angle) * radius) + centerCameraCoordinates.y;
                    double x2 = (Math.cos(angle + angleStep) * radius) + centerCameraCoordinates.x;
                    double y2 = (Math.sin(angle + angleStep) * radius) + centerCameraCoordinates.y;
                    glVertex2d(x1, y1);
                    glVertex2d(x2, y2);
                    angle += angleStep;
                }
                break;
            default:
                break;
        }

        glEnd();
    }
}