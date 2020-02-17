package entities;

import main.Coordinates;
import main.GameStatus;
import main.MyOpenGL;
import main.Parameters;
import org.lwjgl.opengl.ARBShaderObjects;
import utils.MathUtils;

import static org.lwjgl.opengl.GL11.*;

public class ConeAttack {
    private double[] vertex1;
    private double[] vertex2;
    private double[] vertex3;

    public ConeAttack(double[] pointingVector, float coneWidth, float coneLength) {
        pointingVector = MathUtils.normalizeVector(pointingVector);
        double[] v2 = MathUtils.generateOrthonormalVector(pointingVector);
        v2 = MathUtils.normalizeVector(v2);

        vertex1 = new Coordinates(Character.getInstance().getCurrentCoordinates().x + (pointingVector[0] * coneLength) + (v2[0] * coneWidth),
                Character.getInstance().getCurrentCoordinates().y + (pointingVector[1] * coneLength) + (v2[1] * coneWidth)).toCameraCoordinates();
        vertex2 = new Coordinates(Character.getInstance().getCurrentCoordinates().x + (pointingVector[0] * coneLength) - (v2[0] * coneWidth),
                Character.getInstance().getCurrentCoordinates().y + (pointingVector[1] * coneLength) - (v2[1] * coneWidth)).toCameraCoordinates();
        vertex3 = new Coordinates(Character.getInstance().getCurrentCoordinates().x, Character.getInstance().getCurrentCoordinates().y).toCameraCoordinates();
    }

    public double[] getVertex1() {
        return vertex1;
    }

    public double[] getVertex2() {
        return vertex2;
    }

    public double[] getVertex3() {
        return vertex3;
    }

    public void render() {
        int timeUniformLocation = ARBShaderObjects.glGetUniformLocationARB(MyOpenGL.programShader01, "time");
        int characterCoordinatesUniformLocation = ARBShaderObjects.glGetUniformLocationARB(MyOpenGL.programShader01, "characterCameraCoordinates");
        int cameraZoomUniformLocation = ARBShaderObjects.glGetUniformLocationARB(MyOpenGL.programShader01, "cameraZoom");
        ARBShaderObjects.glUseProgramObjectARB(MyOpenGL.programShader01);
        ARBShaderObjects.glUniform1fARB(timeUniformLocation, (float) GameStatus.getRuntime());

        double[] characterCameraCoordinates = Character.getInstance().getCoordinates().toCameraCoordinates();
        float[] characterCoordinatesUniform = new float[]{(float) characterCameraCoordinates[0], Parameters.getWindowHeight() - (float) characterCameraCoordinates[1]};

        ARBShaderObjects.glUniform2fvARB(characterCoordinatesUniformLocation, characterCoordinatesUniform);
        ARBShaderObjects.glUniform1fARB(cameraZoomUniformLocation, (float) Camera.getZoom());

        glBegin(GL_TRIANGLES);

        glVertex2d(getVertex1()[0], getVertex1()[1]);
        glVertex2d(getVertex2()[0], getVertex2()[1]);
        glVertex2d(getVertex3()[0], getVertex3()[1]);

        glEnd();

        //release the shader
        ARBShaderObjects.glUseProgramObjectARB(0);
    }
}
