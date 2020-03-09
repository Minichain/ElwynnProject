package main;

import entities.*;
import entities.Player;
import particles.ParticleManager;
import scene.Scene;

public class SpecialEffects {
    public static void render() {
        for (GraphicEntity entity : Scene.getInstance().getListOfEntities()) {
            if (entity instanceof Enemy) {
                ((Enemy) entity).drawAttackFX();
            } else if (entity instanceof Player) {
                ((Player) entity).drawAttackFX();
            }
        }

        for (CircleAttack circleAttack : Scene.listOfCircleAttacks) {
            circleAttack.render();
        }

        ParticleManager.getInstance().renderParticles();
    }
}