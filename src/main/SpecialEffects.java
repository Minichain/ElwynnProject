package main;

import entities.Character;

public class SpecialEffects {
    public static void render() {
        Character.getInstance().drawAttackFX();
    }
}
