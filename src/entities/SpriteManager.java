package entities;

import scene.TileMap;

public class SpriteManager {
    private static SpriteManager instance = null;

    /** ENTITIES **/
    public Sprite PLAYER;
    public Sprite ENEMY01;
    public Sprite ENEMY02;
    public Sprite ENEMY03;
    public Sprite TREE01;
    public Sprite TREE02;
    public Sprite TREE03;
    public Sprite BUILDING01;
    public Sprite BUILDING02;
    public Sprite FENCE01;
    public Sprite FENCE02;
    public Sprite FENCE03;
    public Sprite FENCE04;
    public Sprite LIGHT01;
    public Sprite G_CLEF;
    public Sprite QUARTER_NOTE;
    public Sprite EIGHTH_NOTE;
    public Sprite DOUBLE_EIGHTH_NOTE;

    /** TILESET **/
    public Sprite TILESET;

    /** INTERFACE **/
    public Sprite IONIAN_ICON;
    public Sprite DORIAN_ICON;
    public Sprite PHRYGIAN_ICON;
    public Sprite LYDIAN_ICON;
    public Sprite MIXOLYDIAN_ICON;
    public Sprite AEOLIAN_ICON;
    public Sprite LOCRIAN_ICON;

    public SpriteManager() {
        /** PLAYER **/
        PLAYER = new Sprite("res/sprites/dynamic/player01.png", 32, 32, 1, 8, 2, 1, 7);

        /** ENEMY **/
        ENEMY01 = new Sprite("res/sprites/dynamic/enemy01.png", 32, 32, 1, 8, 2, 1, -1);
        ENEMY02 = new Sprite("res/sprites/dynamic/enemy02.png", 32, 32, 1, 8, 2, 1, -1);
        ENEMY03 = new Sprite("res/sprites/dynamic/enemy03.png", 32, 32, 1, 8, 2, 1, -1);

        /** MUSICAL NOTES **/
        G_CLEF = new Sprite("res/sprites/dynamic/g_clef_white.png", 16, 16, -1, -1, -1, -1, -1);
        QUARTER_NOTE = new Sprite("res/sprites/dynamic/quarter_note_white.png", 16, 16, -1, -1, -1, -1, -1);
        EIGHTH_NOTE = new Sprite("res/sprites/dynamic/eighth_note_white.png", 16, 16, -1, -1, -1, -1, -1);
        DOUBLE_EIGHTH_NOTE = new Sprite("res/sprites/dynamic/double_eighth_note_white.png", 16, 16, -1, -1, -1, -1, -1);

        /** TREES **/
        TREE01 = new Sprite("res/sprites/static/tree01.png", 16, 64, -1, -1, -1, -1, -1);
        TREE02 = new Sprite("res/sprites/static/tree02.png", 16, 64, -1, -1, -1, -1, -1);
        TREE03 = new Sprite("res/sprites/static/tree03.png", 32, 64, -1, -1, -1, -1, -1);

        /** BUILDINGS **/
        BUILDING01 = new Sprite("res/sprites/static/building01.png", 64, 64, -1, -1, -1, -1, -1);
        BUILDING02 = new Sprite("res/sprites/static/building02.png", 96, 80, -1, -1, -1, -1, -1);

        /** FENCES **/
        FENCE01 = new Sprite("res/sprites/static/fence01.png", 16, 16, -1, -1, -1, -1, -1);
        FENCE02 = new Sprite("res/sprites/static/fence02.png", 16, 16, -1, -1, -1, -1, -1);
        FENCE03 = new Sprite("res/sprites/static/fence03.png", 16, 16, -1, -1, -1, -1, -1);
        FENCE04 = new Sprite("res/sprites/static/fence04.png", 16, 16, -1, -1, -1, -1, -1);

        /** LIGHTS **/
        LIGHT01 = new Sprite("res/sprites/static/light01.png", 16, 64, -1, -1, -1, -1, -1);

        /** TILESET **/
        TILESET = new Sprite("res/sprites/tiles/tileset.png", TileMap.TILE_WIDTH, TileMap.TILE_HEIGHT);

        /** INTERFACE **/
        IONIAN_ICON = new Sprite("res/sprites/interface/ionian_icon.png", 32, 32, -1, -1, -1, -1, -1);
        DORIAN_ICON = new Sprite("res/sprites/interface/dorian_icon.png", 32, 32, -1, -1, -1, -1, -1);
        PHRYGIAN_ICON = new Sprite("res/sprites/interface/phrygian_icon.png", 32, 32, -1, -1, -1, -1, -1);
        LYDIAN_ICON = new Sprite("res/sprites/interface/lydian_icon.png", 32, 32, -1, -1, -1, -1, -1);
        MIXOLYDIAN_ICON = new Sprite("res/sprites/interface/mixolydian_icon.png", 32, 32, -1, -1, -1, -1, -1);
        AEOLIAN_ICON = new Sprite("res/sprites/interface/aeolian_icon.png", 32, 32, -1, -1, -1, -1, -1);
        LOCRIAN_ICON = new Sprite("res/sprites/interface/locrian_icon.png", 32, 32, -1, -1, -1, -1, -1);
    }

    public static SpriteManager getInstance() {
        if (instance == null) {
            instance = new SpriteManager();
        }
        return instance;
    }

    public static int numOfStaticEntitySprites = 10;

    public static Sprite getStaticEntitySprite(int i) {
        Sprite sprite;
        switch (i % numOfStaticEntitySprites) {
            case 0:
                sprite = SpriteManager.getInstance().TREE01;
                break;
            case 1:
                sprite = SpriteManager.getInstance().TREE02;
                break;
            case 2:
                sprite = SpriteManager.getInstance().TREE03;
                break;
            case 3:
                sprite = SpriteManager.getInstance().BUILDING01;
                break;
            case 4:
                sprite = SpriteManager.getInstance().BUILDING02;
                break;
            case 5:
                sprite = SpriteManager.getInstance().FENCE01;
                break;
            case 6:
                sprite = SpriteManager.getInstance().FENCE02;
                break;
            case 7:
                sprite = SpriteManager.getInstance().FENCE03;
                break;
            case 8:
                sprite = SpriteManager.getInstance().FENCE04;
                break;
            case 9:
            default:
                sprite = SpriteManager.getInstance().LIGHT01;
                break;
        }
        return sprite;
    }
}
