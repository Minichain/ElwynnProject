package main;

import entities.*;
import entities.Character;

public class SpecialEffects {
    public static void render() {
        for (Entity entity : Scene.getInstance().getListOfEntities()) {
            if (entity instanceof Enemy) {
                ((Enemy) entity).drawAttackFX();
            } else if (entity instanceof Character) {
                ((Character) entity).drawAttackFX();
            }
        }

        for (CircleAttack circleAttack : Scene.listOfCircleAttacks) {
            circleAttack.render();
        }
    }
}