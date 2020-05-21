package particles;

import main.OpenGLManager;
import scene.Scene;

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
        glDisable(GL_TEXTURE_2D);
        OpenGLManager.glBegin(GL_TRIANGLES);
        for (Particle particle : listOfParticles) {
            particle.render();
        }
        glEnd();
        glEnable(GL_TEXTURE_2D);
    }
}
