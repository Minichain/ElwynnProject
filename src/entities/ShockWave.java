package entities;

import main.Coordinates;
import particles.Particle;
import particles.ParticleManager;
import scene.Camera;
import scene.Scene;
import utils.MathUtils;

public class ShockWave {
    private Coordinates center;
    private double[] direction;
    private float radius;
    private float maxRadius;
    private final float travellingSpeed;
    private boolean dead = false;
    private int numberOfVertices = 64;
    private double angleStep = 2.0 * Math.PI / (double) numberOfVertices;
    private MusicalMode musicalMode;
    private MusicalNote musicalNote;
    private float damage;

    public ShockWave(Coordinates coordinates, double[] direction, MusicalMode musicalMode, MusicalNote musicalNote, float damage) {
        this.center = new Coordinates(coordinates.x, coordinates.y);
        this.direction = direction;
        this.radius = 1f;
        this.maxRadius = 150f;
        this.travellingSpeed = 0.2f;
        this.musicalMode = musicalMode;
        this.musicalNote = musicalNote;
        this.damage = damage;
        this.direction = MathUtils.normalizeVector(this.direction);
    }

    public void update(long timeElapsed) {
        this.radius += timeElapsed * travellingSpeed;
        this.dead = this.radius >= maxRadius;

        double angle = 0;
        angle += Math.random() * angleStep;
        float shockWaveIntensity = 1f - (this.radius / maxRadius);

        /** Add Particles **/
        for (int i = 0; i < numberOfVertices; i++) {
            double cos = Math.cos(angle);
            double sin = Math.sin(angle);
            int x = (int) ((cos * this.radius) + this.center.x);
            int y = (int) ((sin * this.radius) + this.center.y);
            angle += angleStep;

            float dotProduct = (float) MathUtils.dotProduct(new double[]{cos, sin}, this.direction);
            if (dotProduct < 0f) dotProduct = 0f;
            if (Math.random() >= 0.5 && dotProduct > 0f) {
                Particle particle = new Particle(new Coordinates(x, y), new double[]{0, -1}, 0.05, 1.5f,
                        musicalMode.getColor(), 100, false, -1f, shockWaveIntensity * dotProduct);
                ParticleManager.getInstance().addParticle(particle);
            }
        }

        /** Check if any enemy has been hit **/
        for (int i = 0; i < Scene.getInstance().getListOfGraphicEntities().size(); i++) {
            GraphicEntity graphicEntity = Scene.getInstance().getListOfGraphicEntities().get(i);
            if (graphicEntity instanceof Enemy) {
                Coordinates entityCoordinates = graphicEntity.getCenterOfMassWorldCoordinates();
                if (Math.abs(MathUtils.module(entityCoordinates, center) - this.radius) < 5) {
                    double[] vectorToEntity = new double[]{entityCoordinates.x - this.center.x, entityCoordinates.y - this.center.y};
                    float dotProduct = (float) MathUtils.dotProduct(MathUtils.normalizeVector(vectorToEntity), this.direction);
                    if (dotProduct >= 0f) {
                        float damage = this.damage * shockWaveIntensity * dotProduct;
                        float shakingIntensity = damage * 0.01f;
                        if (shakingIntensity > 10f) shakingIntensity = 10f;
                        Camera.getInstance().shake(100, shakingIntensity);
                        ((Enemy) graphicEntity).hurt(damage);
                    }
                }
            }
        }
    }

    public boolean isDead() {
        return dead;
    }
}
