package items;

import entities.Sprite;
import main.Strings;

public class GoldCoin extends Item {
    private int cost;

    public GoldCoin() {
        super();
    }

    @Override
    public void use() {

    }

    @Override
    public String getName() {
        return Strings.getString("item_gold_coin");
    }

    @Override
    public int getCost() {
        return cost;
    }

    @Override
    public Sprite getSprite() {
        return ItemType.GOLD_COIN.getSprite();
    }
}
