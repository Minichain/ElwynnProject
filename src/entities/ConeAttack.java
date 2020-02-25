package entities;

import main.*;
import utils.MathUtils;

import java.util.ArrayList;

import static org.lwjgl.opengl.GL11.*;

public class ConeAttack {
    private double[] vertex1;
    private double[] vertex2;
    private double[] vertex3;
    private double angle;
    private float length;
    private ArrayList<Particle> listOfParticles;
    private boolean attacking;

    public ConeAttack(double[] pointingVector, double angle, float coneLength, boolean attacking) {
        this.angle = angle;
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
        this.attacking = attacking;
        pointingVector = MathUtils.normalizeVector(pointingVector);
        double[] rotatedVector;

        vertex1 = new Coordinates(Character.getInstance().getCurrentCoordinates().x, Character.getInstance().getCurrentCoordinates().y).toCameraCoordinates();
        rotatedVector = MathUtils.rotateVector(pointingVector, angle / 2.0);
        vertex2 = new Coordinates(Character.getInstance().getCurrentCoordinates().x + rotatedVector[0] * length, Character.getInstance().getCurrentCoordinates().y + rotatedVector[1] * length).toCameraCoordinates();
        rotatedVector = MathUtils.rotateVector(pointingVector, - angle / 2.0);
        vertex3 = new Coordinates(Character.getInstance().getCurrentCoordinates().x + rotatedVector[0] * length, Character.getInstance().getCurrentCoordinates().y + rotatedVector[1] * length).toCameraCoordinates();

        Particle particle;

        /** GENERATE NEW PARTICLES **/
        if (attacking) {
            Coordinates characterCoordinates = Character.getInstance().getCurrentCoordinates();
            Coordinates particleCoordinates;
            double amountOfParticles = 0.1;
            double randomAngle;
            for (int i = 0; i < (timeElapsed * amountOfParticles); i++) {
                randomAngle = Math.random() * angle;
                rotatedVector = MathUtils.rotateVector(MathUtils.rotateVector(pointingVector, - angle / 2.0), randomAngle);
                double distanceFromCharacter = 40;
                particleCoordinates = new Coordinates(
                        characterCoordinates.x + rotatedVector[0] * Math.random() * distanceFromCharacter,
                        characterCoordinates.y + rotatedVector[1] * Math.random() * distanceFromCharacter);
                particle = new Particle(particleCoordinates, rotatedVector, (int) (4 * Camera.getZoom()), 1f, 1f, 1f);
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

        /** DEBUG LINES **/
        if (attacking && Parameters.isDebugMode()) {
            glDisable(GL_BLEND);
            glBegin(GL_LINES);
            glLineWidth(4);
            glColor4f(1.0f, 1.0f, 1.0f, 1.0f);
            glVertex2d(vertex1[0], vertex1[1]);
            glVertex2d(vertex2[0], vertex2[1]);
            glVertex2d(vertex2[0], vertex2[1]);
            glVertex2d(vertex3[0], vertex3[1]);
            glVertex2d(vertex3[0], vertex3[1]);
            glVertex2d(vertex1[0], vertex1[1]);
            glEnd();
            glEnable(GL_BLEND);
        }

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
