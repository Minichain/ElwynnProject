package entities;

import main.*;
import particles.Particle;
import particles.ParticleManager;
import scene.Scene;
import text.FloatingTextEntity;
import utils.MathUtils;

import java.awt.*;

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
    private MusicalMode musicalMode;

    public ConeAttack(Coordinates initialParticleCoordinates, double[] pointingVector, double angle, float coneLength, int attackPeriod, int attackCoolDown, float attackPower, boolean enemyAttack, boolean attacking) {
        this.angle = angle;
        this.length = coneLength;
        this.attackPeriod = attackPeriod;
        this.attackCoolDown = attackCoolDown;
        this.attackPower = attackPower;
        this.enemyAttack = enemyAttack;
        this.musicalMode = MusicalMode.IONIAN;
        update(initialParticleCoordinates, pointingVector, 0, attacking, musicalMode);
    }

    public void update(Coordinates initialParticleCoordinates, double[] pointingVector, long timeElapsed, boolean attacking, MusicalMode musicalMode) {
        this.attacking = attacking;
        this.musicalMode = musicalMode;
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
            double amountOfParticles = 0.02;
            double randomAngle;
            for (int i = 0; i < (timeElapsed * amountOfParticles); i++) {
                randomAngle = Math.random() * angle;
                rotatedVector = MathUtils.rotateVector(MathUtils.rotateVector(pointingVector, - angle / 2.0), randomAngle);
                double distanceFromEntity = 40;
                particleCoordinates = new Coordinates(
                        initialParticleCoordinates.x + rotatedVector[0] * Math.random() * distanceFromEntity,
                        initialParticleCoordinates.y + rotatedVector[1] * Math.random() * distanceFromEntity);
                if (enemyAttack) {
                    particle = new Particle(particleCoordinates, rotatedVector, 0.25, 4, new Color(1f, 0f, 0f), true);
                } else {
                    particle = new Particle(particleCoordinates, rotatedVector, 0.25, 4, musicalMode.getColor(), true);
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
                    damage *= ((Enemy) entity).getWeakness(musicalMode);
                    ((Enemy) entity).hurt(damage);
                }
            } else if (entity instanceof Player && enemyAttack) {
                if (((Player) entity).getStatus() != Player.Status.DEAD
                        && ((Player) entity).getStatus() != Player.Status.ROLLING
                        && MathUtils.isPointInsideTriangle(entity.getCenterOfMassCameraCoordinates(), vertex1, vertex2, vertex3)) {
                    ((Player) entity).hurt(damage);
                }
            }
        }

        attackCoolDown = attackPeriod;
    }

    public void render() {
        /** DEBUG LINES **/
        if (attacking && Parameters.isDebugMode()) {
            glDisable(GL_TEXTURE_2D);
            glDisable(GL_BLEND);
            OpenGLManager.glBegin(GL_LINES);
            if (enemyAttack) {
                glColor4f(1.0f, 0f, 0f, 1.0f);
            } else {
                glColor4f(musicalMode.getColor().getRed() / 255f, musicalMode.getColor().getGreen() / 255f, musicalMode.getColor().getBlue() / 255f, 1.0f);
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
}
