package entities;

import audio.OpenALManager;
import main.*;
import particles.Particle;
import particles.ParticleManager;
import scene.Camera;
import scene.Scene;
import text.FloatingTextEntity;
import utils.MathUtils;

import static org.lwjgl.opengl.GL11.*;

public class ConeAttack {
    private Coordinates vertex1;
    private Coordinates vertex2;
    private Coordinates vertex3;
    private double angle;
    private float length;
    private boolean attacking;
    private int attackPeriod;
    private int attackCoolDown;
    private float attackPower;
    private boolean enemyAttack;

    public ConeAttack(Coordinates initialParticleCoordinates, double[] pointingVector, double angle, float coneLength, int attackPeriod, int attackCoolDown, float attackPower, boolean enemyAttack, boolean attacking) {
        this.angle = angle;
        this.length = coneLength;
        this.attackPeriod = attackPeriod;
        this.attackCoolDown = attackCoolDown;
        this.attackPower = attackPower;
        this.enemyAttack = enemyAttack;
        update(initialParticleCoordinates, pointingVector, 0, attacking);
    }

    public void update(Coordinates initialParticleCoordinates, double[] pointingVector, long timeElapsed, boolean attacking) {
        this.attacking = attacking;
        pointingVector = MathUtils.normalizeVector(pointingVector);
        double[] rotatedVector;

        vertex1 = new Coordinates(initialParticleCoordinates.x, initialParticleCoordinates.y).toCameraCoordinates();
        rotatedVector = MathUtils.rotateVector(pointingVector, angle / 2.0);
        vertex2 = new Coordinates(initialParticleCoordinates.x + rotatedVector[0] * length, initialParticleCoordinates.y + rotatedVector[1] * length).toCameraCoordinates();
        rotatedVector = MathUtils.rotateVector(pointingVector, - angle / 2.0);
        vertex3 = new Coordinates(initialParticleCoordinates.x + rotatedVector[0] * length, initialParticleCoordinates.y + rotatedVector[1] * length).toCameraCoordinates();

        Particle particle;

        /** GENERATE NEW PARTICLES **/
        if (attacking) {
            Coordinates particleCoordinates;
            double amountOfParticles = 0.1;
            double randomAngle;
            for (int i = 0; i < (timeElapsed * amountOfParticles); i++) {
                randomAngle = Math.random() * angle;
                rotatedVector = MathUtils.rotateVector(MathUtils.rotateVector(pointingVector, - angle / 2.0), randomAngle);
                double distanceFromEntity = 40;
                particleCoordinates = new Coordinates(
                        initialParticleCoordinates.x + rotatedVector[0] * Math.random() * distanceFromEntity,
                        initialParticleCoordinates.y + rotatedVector[1] * Math.random() * distanceFromEntity);
                if (enemyAttack) {
                    particle = new Particle(particleCoordinates, rotatedVector, (int) (4 * Camera.getZoom()), 1f, 0f, 0f);
                } else {
                    particle = new Particle(particleCoordinates, rotatedVector, (int) (4 * Camera.getZoom()), 1f, 1f, 1f);
                }
                ParticleManager.getInstance().addParticle(particle);
            }
        }

        /** DEAL DAMAGE **/
        if (!attacking || attackCoolDown > 0) {
            attackCoolDown -= timeElapsed;
            return;
        }

        GraphicEntity entity;
        for (int i = 0; i < Scene.getInstance().getListOfEntities().size(); i++) {
            entity = Scene.getInstance().getListOfEntities().get(i);
            float damage = (float) (attackPower + (Math.random() * 10));

            if (entity instanceof Enemy && !enemyAttack) {
                if (((Enemy) entity).getStatus() != Enemy.Status.DEAD
                        && MathUtils.isPointInsideTriangle(entity.getCenterOfMassCameraCoordinates(), vertex1, vertex2, vertex3)) {
                    ((Enemy) entity).setHealth(((Enemy) entity).getHealth() - damage);
                    String text = String.valueOf((int) damage);
                    new FloatingTextEntity(entity.getCenterOfMassWorldCoordinates().x, entity.getCenterOfMassWorldCoordinates().y, text, true, true, false);
                }
            } else if (entity instanceof Player && enemyAttack) {
                if (((Player) entity).getStatus() != Player.Status.DEAD
                        && MathUtils.isPointInsideTriangle(entity.getCenterOfMassCameraCoordinates(), vertex1, vertex2, vertex3)) {
                    ((Player) entity).setHealth(((Player) entity).getHealth() - damage);
                    OpenALManager.playSound(OpenALManager.SOUND_PLAYER_HURT_01);
                    String text = String.valueOf((int) damage);
                    new FloatingTextEntity(entity.getCenterOfMassWorldCoordinates().x, entity.getCenterOfMassWorldCoordinates().y, text, true, true, true);
                }
            }
        }

        OpenALManager.playSound(OpenALManager.SOUND_ATTACK_01);
        attackCoolDown = attackPeriod;
    }

    public void render() {
        glDisable(GL_TEXTURE_2D);

        /** DEBUG LINES **/
        if (attacking && Parameters.isDebugMode()) {
            glDisable(GL_BLEND);
            OpenGLManager.glBegin(GL_LINES);
            if (enemyAttack) {
                glColor4f(1.0f, 0f, 0f, 1.0f);
            } else {
                glColor4f(1.0f, 1.0f, 1.0f, 1.0f);
            }
            glVertex2d(vertex1.x, vertex1.y);
            glVertex2d(vertex2.x, vertex2.y);
            glVertex2d(vertex2.x, vertex2.y);
            glVertex2d(vertex3.x, vertex3.y);
            glVertex2d(vertex3.x, vertex3.y);
            glVertex2d(vertex1.x, vertex1.y);
            glEnd();
            glEnable(GL_BLEND);
        }
    }

    /*
    public void render() {
        int timeUniformLocation = ARBShaderObjects.glGetUniformLocationARB(MyOpenGL.programShader01, "time");
        int playerCoordinatesUniformLocation = ARBShaderObjects.glGetUniformLocationARB(MyOpenGL.programShader01, "playerCameraCoordinates");
        int cameraZoomUniformLocation = ARBShaderObjects.glGetUniformLocationARB(MyOpenGL.programShader01, "cameraZoom");
        int cameraWindowRatioUniformLocation = ARBShaderObjects.glGetUniformLocationARB(MyOpenGL.programShader01, "cameraWindowRatio");
        ARBShaderObjects.glUseProgramObjectARB(MyOpenGL.programShader01);
        ARBShaderObjects.glUniform1fARB(timeUniformLocation, (float) GameStatus.getRuntime());

        double[] playerWindowCoordinates = Player.getInstance().getCoordinates().toCameraCoordinates();
        playerWindowCoordinates[0] *= Window.getCameraWindowScaleFactor()[0];
        playerWindowCoordinates[1] *= Window.getCameraWindowScaleFactor()[1];
        float[] playerCoordinatesUniform = new float[]{(float) playerWindowCoordinates[0], Window.getHeight() - (float) playerWindowCoordinates[1]};

        ARBShaderObjects.glUniform2fvARB(playerCoordinatesUniformLocation, playerCoordinatesUniform);
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
