package items;

import audio.OpenALManager;
import entities.Player;
import entities.Sprite;
import main.Strings;

public class HealthPotion extends Item {
    private int cost;
    private float restoreValue;

    public HealthPotion() {
        super();
        cost = 10;
        restoreValue = 500f;
        MAX_AMOUNT_PER_STACK = 5;
    }

    @Override
    public void use() {
        Player.getInstance().setHealth(Player.getInstance().getHealth() + restoreValue);
        OpenALManager.playSound(OpenALManager.SOUND_DRINK_01);
    }

    @Override
    public String getName() {
        return Strings.getString("item_health_potion");
    }

    @Override
    public int getCost() {
        return cost;
    }

    @Override
    public Sprite getSprite() {
        return ItemType.HEALTH_POTION.getSprite();
    }
}
