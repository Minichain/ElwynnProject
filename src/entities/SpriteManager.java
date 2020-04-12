package entities;

import scene.TileMap;

public class SpriteManager {
    private static SpriteManager instance = null;

    /** SPRITES **/
    public Sprite PLAYER;
    public Sprite ENEMY;
    public Sprite TREE01;
    public Sprite TREE02;
    public Sprite TREE03;
    public Sprite BUILDING01;
    public Sprite FENCE01;
    public Sprite FENCE02;
    public Sprite FENCE03;
    public Sprite TILESET;

    public SpriteManager() {
        /** PLAYER **/
        PLAYER = new Sprite("res/sprites/dynamic/player.png", 32, 32, 1, 8, 2, 1, 7);

        /** ENEMY **/
        ENEMY = new Sprite("res/sprites/dynamic/enemy.png", 32, 32, 1, 8, 2, 1, -1);

        /** TREES **/
        TREE01 = new Sprite("res/sprites/static/tree01.png", 16, 64, -1, -1, -1, -1, -1);
        TREE02 = new Sprite("res/sprites/static/tree02.png", 16, 64, -1, -1, -1, -1, -1);
        TREE03 = new Sprite("res/sprites/static/tree03.png", 32, 64, -1, -1, -1, -1, -1);

        /** BUILDINGS **/
        BUILDING01 = new Sprite("res/sprites/static/building01.png", 64, 64, -1, -1, -1, -1, -1);

        /** FENCES **/
        FENCE01 = new Sprite("res/sprites/static/fence01.png", 16, 16, -1, -1, -1, -1, -1);
        FENCE02 = new Sprite("res/sprites/static/fence02.png", 16, 16, -1, -1, -1, -1, -1);
        FENCE03 = new Sprite("res/sprites/static/fence03.png", 16, 16, -1, -1, -1, -1, -1);

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
