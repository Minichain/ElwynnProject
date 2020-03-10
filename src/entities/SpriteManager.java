package entities;

import scene.TileMap;

public class SpriteManager {
    private static SpriteManager instance = null;

    /** SPRITES **/
    public Sprite PLAYER;
    public Sprite ENEMY;
    public Sprite TREE;
    public Sprite BUILDING;
    public Sprite TILESET;

    public SpriteManager() {
        /** PLAYER **/
        PLAYER = new Sprite("res/sprites/dynamic/player.png", 32, 32, 1, 1, 1, 1);

        /** ENEMY **/
        ENEMY = new Sprite("res/sprites/dynamic/enemy.png", 32, 32, 1, 1, 1, 1);

        /** TREE **/
        TREE = new Sprite("res/sprites/static/tree01.png", 16, 64, -1, -1, -1, -1);

        /** BUILDING **/
        BUILDING = new Sprite("res/sprites/static/building01.png", 64, 64, -1, -1, -1, -1);

        /** TILESET **/
        TILESET = new Sprite("res/sprites/tiles/tileset.png", TileMap.TILE_WIDTH, TileMap.TILE_HEIGHT);
    }

    public static SpriteManager getInstance() {
        if (instance == null) {
            instance = new SpriteManager();
        }
        return instance;
    }
}
