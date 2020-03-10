package entities;

public class SpriteManager {
    private static SpriteManager instance = null;

    /** SPRITES **/
    public Sprite PLAYER;
    public Sprite ENEMY;
    public Sprite TREE;

    public SpriteManager() {
        /** PLAYER **/
        PLAYER = new Sprite("res/sprites/dynamic/link.png", 32, 32, 1, 8, 1, 1);

        /** ENEMY **/
        ENEMY = new Sprite("res/sprites/dynamic/enemy.png", 32, 32, 1, 8, 1, 1);

        /** TREE **/
        TREE = new Sprite("res/sprites/static/tree01.png", 16, 64, -1, -1, -1, -1);
    }

    public static SpriteManager getInstance() {
        if (instance == null) {
            instance = new SpriteManager();
        }
        return instance;
    }
}
