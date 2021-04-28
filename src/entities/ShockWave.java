package entities;

import main.Coordinates;
import particles.Particle;
import particles.ParticleManager;
import scene.Camera;
import scene.Scene;
import utils.MathUtils;

public class ShockWave {
    private Coordinates center;
    private float radius;
    private float maxRadius;
    private boolean dead = false;
    private int numberOfVertices = 32;
    private double angleStep = 2.0 * Math.PI / (double) numberOfVertices;
    private MusicalMode musicalMode;
    private MusicalNote musicalNote;
    private float damage;

    public ShockWave(Coordinates coordinates, MusicalMode musicalMode, MusicalNote musicalNote, float damage) {
        this.center = new Coordinates(coordinates.x, coordinates.y);
        this.radius = 1f;
        this.maxRadius = 100f;
        this.musicalMode = musicalMode;
        this.musicalNote = musicalNote;
        this.damage = damage;
    }

    public void update(long timeElapsed) {
        this.radius += timeElapsed * 0.1f;
        this.dead = this.radius >= maxRadius;

        double angle = 0;
        angle += Math.random() * angleStep;
        float shockWaveIntensity = 1f - (this.radius / maxRadius);
        for (int i = 0; i < numberOfVertices; i++) {
            int x = (int) ((Math.cos(angle) * this.radius) + this.center.x);
            int y = (int) ((Math.sin(angle) * this.radius) + this.center.y);
            angle += angleStep;
            if (Math.random() >= 0.5) {
                Particle particle = new Particle(new Coordinates(x, y), new double[]{0, -1}, 0.05, 1.5f,
                        musicalMode.getColor(), 100, false, -1f, shockWaveIntensity);
                ParticleManager.getInstance().addParticle(particle);
            }
        }

        for (int i = 0; i < Scene.getInstance().getListOfGraphicEntities().size(); i++) {
            GraphicEntity graphicEntity = Scene.getInstance().getListOfGraphicEntities().get(i);
            if (graphicEntity instanceof Enemy) {
                if (Math.abs(MathUtils.module(graphicEntity.getCenterOfMassWorldCoordinates(), center) - this.radius) < 5) {
                    float damage = this.damage * Player.getInstance().getWeakness(musicalNote) * shockWaveIntensity;
                    ((Enemy) graphicEntity).hurt(damage);
                }
            }
        }
    }

    public boolean isDead() {
        return dead;
    }
}
