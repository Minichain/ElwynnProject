package main;

import entities.Character;
import entities.Enemy;
import entities.Entity;
import entities.Scene;

public class SpecialEffects {
    public static void render() {
        for (Entity entity : Scene.getInstance().getListOfEntities()) {
            if (entity instanceof Enemy) {
                ((Enemy) entity).drawAttackFX();
            } else if (entity instanceof Character) {
                ((Character) entity).drawAttackFX();
            }
        }
    }
}
