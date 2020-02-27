package entities;

import audio.OpenALManager;
import main.Coordinates;
import main.Parameters;
import utils.MathUtils;

import java.util.ArrayList;

import static org.lwjgl.opengl.GL11.*;
import static org.lwjgl.opengl.GL11.glEnd;

public class CircleAttack {
    private Coordinates[] vertices;
    private int numberOfVertices = 16;
    private Coordinates center;
    private double radius;
    private double angleStep = Math.PI / ((double) numberOfVertices / 2.0);
    private ArrayList<Particle> listOfParticles;
    private boolean attacking;
    private int attackPeriod;
    private int attackCoolDown;
    private float attackPower;
    private boolean enemyAttack;

    public CircleAttack(Coordinates center, double radius, int attackPeriod, int attackCoolDown, float attackPower, boolean enemyAttack, boolean attacking) {
        vertices = new Coordinates[numberOfVertices];
        this.center = center;
        this.radius = radius;
        this.enemyAttack = enemyAttack;
        this.attackPeriod = attackPeriod;
        this.attackCoolDown = attackCoolDown;
        this.attackPower = attackPower;
        this.listOfParticles = new ArrayList<>();
        update(0, attacking);
    }

    public void update(long timeElapsed, boolean attacking) {
        this.attacking = attacking;

        /** CIRCLE OUTLINE **/
        double angle = 0;
        for (int i = 0; i < numberOfVertices; i++) {
            double x = (Math.cos(angle) * radius) + this.center.x;
            double y = (Math.sin(angle) * radius) + this.center.y;
            vertices[i] = new Coordinates(x, y);
            angle += angleStep;
        }

        Particle particle;
        double[] velocityVector;

        /** GENERATE NEW PARTICLES **/
        if (attacking) {
            Coordinates particleCoordinates;
            double amountOfParticles = 1;
            double randomAngle;
            double[] generationVector;
            for (int i = 0; i < (timeElapsed * amountOfParticles); i++) {
                randomAngle = Math.random() * 2.0 * Math.PI;
                generationVector = new double[]{radius * Math.random(), 0};
                generationVector = MathUtils.rotateVector(generationVector, randomAngle);
                velocityVector = new double[]{0, -0.1};
                particleCoordinates = new Coordinates(this.center.x + generationVector[0], this.center.y + generationVector[1]);
                if (enemyAttack) {
                    particle = new Particle(particleCoordinates, velocityVector, (int) (4 * Camera.getZoom()), 1f, 0f, 0f);
                } else {
                    particle = new Particle(particleCoordinates, velocityVector, (int) (4 * Camera.getZoom()), 1f, 1f, 1f);
                }
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

        /** DEAL DAMAGE **/
        if (!attacking || attackCoolDown > 0) {
            attackCoolDown -= timeElapsed;
            return;
        }

        Entity entity;
        for (int i = 0; i < Scene.getInstance().getListOfEntities().size(); i++) {
            entity = Scene.getInstance().getListOfEntities().get(i);
            double[] entityCameraCoords = entity.getCoordinates().toCameraCoordinates();
            float damage = (float) (attackPower + (Math.random() * 10));

            if (entity instanceof Enemy && !enemyAttack) {
                if (((Enemy) entity).getStatus() != Enemy.Status.DEAD
                        && MathUtils.isPointInsideCircle(new double[]{entityCameraCoords[0], entityCameraCoords[1]}, this.center.toCameraCoordinates(), radius)) {
                    ((Enemy) entity).setHealth(((Enemy) entity).getHealth() - damage);
                    String text = String.valueOf((int) damage);
                    new FloatingTextEntity(entity.getCoordinates().x, entity.getCoordinates().y, text, true, true, false);
                }
            } else if (entity instanceof Character && enemyAttack) {
                if (((Character) entity).getStatus() != Character.Status.DEAD
                        && MathUtils.isPointInsideCircle(new double[]{entityCameraCoords[0], entityCameraCoords[1]}, this.center.toCameraCoordinates(), radius)) {
                    ((Character) entity).setHealth(((Character) entity).getHealth() - damage);
                    String text = String.valueOf((int) damage);
                    new FloatingTextEntity(entity.getCoordinates().x, entity.getCoordinates().y, text, true, true, true);
                }
            }
        }

        OpenALManager.playSound(OpenALManager.SOUND_LINK_DASH);
        attackCoolDown = attackPeriod;
    }

    public void render() {
        glDisable(GL_TEXTURE_2D);

        /** DEBUG LINES **/
        if (attacking && Parameters.isDebugMode()) {
            glDisable(GL_BLEND);
            glBegin(GL_LINES);
            glLineWidth(4);
            if (enemyAttack) {
                glColor4f(1.0f, 0f, 0f, 1.0f);
            } else {
                glColor4f(1.0f, 1.0f, 1.0f, 1.0f);
            }

            for (int i = 1; i < vertices.length; i++) {
                glVertex2d(vertices[i - 1].x, vertices[i - 1].y);
                glVertex2d(vertices[i].x, vertices[i].y);
            }

            glEnd();
            glEnable(GL_BLEND);
        }

        glBegin(GL_TRIANGLES);
        for (int i = 0; i < listOfParticles.size(); i++) {
            listOfParticles.get(i).render();
        }
        glEnd();
    }
}

