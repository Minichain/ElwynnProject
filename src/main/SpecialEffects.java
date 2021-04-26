package main;

import entities.*;
import entities.Player;
import particles.ParticleManager;
import scene.Scene;

public class SpecialEffects {
    public static void render() {
        OpenGLManager.releaseCurrentShader();
//        Log.l("Render Special Effects");

        for (GraphicEntity entity : Scene.getInstance().getListOfGraphicEntities()) {
            if (entity instanceof Enemy) {
                ((Enemy) entity).drawAttackFX();
            } else if (entity instanceof Player) {
                ((Player) entity).drawAttackFX();
            }
        }

        ParticleManager.getInstance().renderParticles();
    }
}