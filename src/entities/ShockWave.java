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

    public ShockWave(Coordinates coordinates, MusicalMode musicalMode, MusicalNote musicalNote) {
        this.center = coordinates;
        this.radius = 2.5f;
        this.maxRadius = 50f;
        this.musicalMode = musicalMode;
        this.musicalNote = musicalNote;
    }

    public void update(long timeElapsed) {
        this.radius += timeElapsed * 0.025f;
        this.dead = this.radius >= maxRadius;

        double angle = 0;
        double radius = this.radius * Camera.getZoom();
        angle += Math.random() * angleStep;
        for (int i = 0; i < numberOfVertices; i++) {
            int x = (int) ((Math.cos(angle) * radius) + this.center.x);
            int y = (int) ((Math.sin(angle) * radius) + this.center.y);
            angle += angleStep;
            if (Math.random() >= 0.5) {
                Particle particle = new Particle(new Coordinates(x, y), new double[]{0, -1}, 0.05, 1.5f,
                        musicalMode.getColor(), 100, false, -1f, 1f - (this.radius / maxRadius));
                ParticleManager.getInstance().addParticle(particle);
            }
        }

        for (int i = 0; i < Scene.getInstance().getListOfGraphicEntities().size(); i++) {
            GraphicEntity graphicEntity = Scene.getInstance().getListOfGraphicEntities().get(i);
            if (graphicEntity instanceof Enemy) {
                if (Math.abs(MathUtils.module(graphicEntity.getCenterOfMassWorldCoordinates(), center) - this.radius) < 1) {
                    float damage = 500f * Player.getInstance().getWeakness(musicalNote);
                    ((Enemy) graphicEntity).hurt(damage);
                }
            }
        }
    }

    public boolean isDead() {
        return dead;
    }
}
