package particles;

import main.Coordinates;
import main.OpenGLManager;
import scene.Scene;
import utils.MathUtils;

import java.awt.*;
import java.util.ArrayList;

import static org.lwjgl.opengl.GL11.*;

public class ParticleManager {
    private static ParticleManager instance = null;
    private ArrayList<Particle> listOfParticles;

    public ParticleManager() {
        listOfParticles = new ArrayList<>();
    }

    public static ParticleManager getInstance() {
        if (instance == null) {
            instance = new ParticleManager();
        }
        return instance;
    }

    public void addParticle(Particle particle) {
        listOfParticles.add(particle);
    }

    public void addParticles(Particle[] particles) {
        for (int i = 0; i < particles.length; i++) {
            addParticle(particles[i]);
        }
    }

    public void updateParticles(long timeElapsed) {
        Particle particle;
        for (int i = 0; i < listOfParticles.size(); i++) {
            particle = listOfParticles.get(i);
            particle.update(timeElapsed);
            if (particle.isDead()) {
                Scene.getInstance().getListOfLightSources().remove(particle.getLightSource());
                listOfParticles.remove(particle);
            }
        }
    }

    public void renderParticles() {
        glEnable(GL_BLEND);
        OpenGLManager.glBegin(GL_TRIANGLES);
        for (Particle particle : listOfParticles) {
            particle.render();
        }
        glEnd();
        glDisable(GL_BLEND);
    }

    public static void particlesExplosion(Coordinates position, int numberOfParticles, Color color) {
        double[] velocityVector;
        for (int i = 0; i < numberOfParticles; i++) {
            velocityVector = MathUtils.rotateVector(new double[]{1.0, 0.0}, MathUtils.random(0, 2 * Math.PI));
            ParticleManager.getInstance().addParticle(new Particle(position, velocityVector, 0.05, 1f, color,
                    500, true, 2.5f));
        }
    }
}
