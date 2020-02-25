package entities;

import main.*;
import utils.MathUtils;

import java.util.ArrayList;

import static org.lwjgl.opengl.GL11.*;

public class ConeAttack {
    private double[] vertex1;
    private double[] vertex2;
    private double[] vertex3;
    private float width;
    private float length;
    private ArrayList<Particle> listOfParticles;

    public ConeAttack(double[] pointingVector, float coneWidth, float coneLength, boolean attacking) {
        this.width = coneWidth;
        this.length = coneLength;
        this.listOfParticles = new ArrayList<>();
        update(pointingVector, 0, attacking);
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

    public void update(double[] pointingVector, long timeElapsed, boolean attacking) {
        pointingVector = MathUtils.normalizeVector(pointingVector);
        double[] v2 = MathUtils.generateOrthonormalVector(pointingVector);
        v2 = MathUtils.normalizeVector(v2);

        vertex1 = new Coordinates(Character.getInstance().getCurrentCoordinates().x + (pointingVector[0] * length) + (v2[0] * width),
                Character.getInstance().getCurrentCoordinates().y + (pointingVector[1] * length) + (v2[1] * width)).toCameraCoordinates();
        vertex2 = new Coordinates(Character.getInstance().getCurrentCoordinates().x + (pointingVector[0] * length) - (v2[0] * width),
                Character.getInstance().getCurrentCoordinates().y + (pointingVector[1] * length) - (v2[1] * width)).toCameraCoordinates();
        vertex3 = new Coordinates(Character.getInstance().getCurrentCoordinates().x, Character.getInstance().getCurrentCoordinates().y).toCameraCoordinates();

        Particle particle;

        /** GENERATE NEW PARTICLES **/
        if (attacking) {
            Coordinates characterCoordinates = Character.getInstance().getCurrentCoordinates();
            Coordinates particleCoordinates;
            double amountOfParticles = 0.25;
            for (int i = 0; i < (timeElapsed * amountOfParticles); i++) {
                double randomAngle = Math.random() * Math.PI / 4.0;
                double[] vector = MathUtils.rotateVector(MathUtils.rotateVector(pointingVector, - Math.PI / 8.0), randomAngle);
                double distanceFromCharacter = 40;
                particleCoordinates = new Coordinates(
                        characterCoordinates.x + vector[0] * Math.random() * distanceFromCharacter,
                        characterCoordinates.y + vector[1] * Math.random() * distanceFromCharacter);
                particle = new Particle(particleCoordinates, vector, (int) (4 * Camera.getZoom()), 1f, 1f, 1f);
                listOfParticles.add(particle);
            }
        }

        /** UPDATE PARTICLES **/
        for (int i = 0; i < listOfParticles.size(); i++) {
            particle = listOfParticles.get(i);
            particle.update(timeElapsed);
            if (particle.isDead()) {
                listOfParticles.remove(particle);
            }
        }
    }

    public void render() {
        glDisable(GL_TEXTURE_2D);
        glBegin(GL_TRIANGLES);
        for (int i = 0; i < listOfParticles.size(); i++) {
            listOfParticles.get(i).render();
        }
        glEnd();
    }

    /*
    public void render() {
        int timeUniformLocation = ARBShaderObjects.glGetUniformLocationARB(MyOpenGL.programShader01, "time");
        int characterCoordinatesUniformLocation = ARBShaderObjects.glGetUniformLocationARB(MyOpenGL.programShader01, "characterCameraCoordinates");
        int cameraZoomUniformLocation = ARBShaderObjects.glGetUniformLocationARB(MyOpenGL.programShader01, "cameraZoom");
        int cameraWindowRatioUniformLocation = ARBShaderObjects.glGetUniformLocationARB(MyOpenGL.programShader01, "cameraWindowRatio");
        ARBShaderObjects.glUseProgramObjectARB(MyOpenGL.programShader01);
        ARBShaderObjects.glUniform1fARB(timeUniformLocation, (float) GameStatus.getRuntime());

        double[] characterWindowCoordinates = Character.getInstance().getCoordinates().toCameraCoordinates();
        characterWindowCoordinates[0] *= Window.getCameraWindowScaleFactor()[0];
        characterWindowCoordinates[1] *= Window.getCameraWindowScaleFactor()[1];
        float[] characterCoordinatesUniform = new float[]{(float) characterWindowCoordinates[0], Window.getHeight() - (float) characterWindowCoordinates[1]};

        ARBShaderObjects.glUniform2fvARB(characterCoordinatesUniformLocation, characterCoordinatesUniform);
        ARBShaderObjects.glUniform1fARB(cameraZoomUniformLocation, (float) Camera.getZoom());
        ARBShaderObjects.glUniform2fvARB(cameraWindowRatioUniformLocation, Window.getCameraWindowScaleFactor());

        glBegin(GL_TRIANGLES);

        glVertex2d(getVertex1()[0], getVertex1()[1]);
        glVertex2d(getVertex2()[0], getVertex2()[1]);
        glVertex2d(getVertex3()[0], getVertex3()[1]);

        glEnd();

        //release the shader
        ARBShaderObjects.glUseProgramObjectARB(0);
    }
    */
}
