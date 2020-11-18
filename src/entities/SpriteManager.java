package entities;

import scene.TileMap;

public class SpriteManager {
    private static SpriteManager instance = null;

    /** ENTITIES **/
    public Sprite PLAYER, SMOKE01, NOTCH, ENEMY01, ENEMY02, ENEMY03, ENEMY04, ENEMY05, ENEMY06, ENEMY07,
            TREE01, TREE02, TREE03, TREE04, BUILDING01, BUILDING02, FENCE01, FENCE02, FENCE03, FENCE04,
            LIGHT01, TORCH01, G_CLEF, QUARTER_NOTE, EIGHTH_NOTE, DOUBLE_EIGHTH_NOTE, GOLD_COIN,
            A_CONTROLLER_BUTTON, F_KEYBOARD_KEY;

    /** TILESET **/
    public Sprite TILESET;

    /** INTERFACE **/
    public Sprite IONIAN_ICON, DORIAN_ICON, PHRYGIAN_ICON, LYDIAN_ICON, MIXOLYDIAN_ICON, AEOLIAN_ICON, LOCRIAN_ICON,
            HEALTH_BAR, MANA_BAR, STAMINA_BAR, EMPTY_BAR, HEALTH_POTION, MANA_POTION, HASTE_POTION, GOLD_COIN_INTERFACE;

    public SpriteManager() {
        /** PLAYER **/
        PLAYER = new Sprite("res/sprites/dynamic/player01.png", 19, 20, 1, 8, 2, 1, 7, 3);

        /** NPC **/
        NOTCH = new Sprite("res/sprites/dynamic/notch.png", 19, 20, 1, 8, 2, 1, 7, 3);

        /** ENEMY **/
        ENEMY01 = new Sprite("res/sprites/dynamic/enemy01.png", 19, 20, 1, 8, 2, 1, 7, 3);
        ENEMY02 = new Sprite("res/sprites/dynamic/enemy02.png", 19, 20, 1, 8, 2, 1, 7, 3);
        ENEMY03 = new Sprite("res/sprites/dynamic/enemy03.png", 19, 20, 1, 8, 2, 1, 7, 3);
        ENEMY04 = new Sprite("res/sprites/dynamic/enemy04.png", 19, 20, 1, 8, 2, 1, 7, 3);
        ENEMY05 = new Sprite("res/sprites/dynamic/enemy05.png", 19, 20, 1, 8, 2, 1, 7, 3);
        ENEMY06 = new Sprite("res/sprites/dynamic/enemy06.png", 19, 20, 1, 8, 2, 1, 7, 3);
        ENEMY07 = new Sprite("res/sprites/dynamic/enemy07.png", 19, 20, 1, 8, 2, 1, 7, 3);

        /** MUSICAL NOTES **/
        G_CLEF = new Sprite("res/sprites/dynamic/g_clef_white_8x8.png", 8, 8, -1);
        QUARTER_NOTE = new Sprite("res/sprites/dynamic/quarter_note_white_8x8.png", 8, 8, -1);
        EIGHTH_NOTE = new Sprite("res/sprites/dynamic/eighth_note_white_8x8.png", 8, 8, -1);
        DOUBLE_EIGHTH_NOTE = new Sprite("res/sprites/dynamic/double_eighth_note_white_8x8.png", 8, 8, -1);

        /** TREES **/
        TREE01 = new Sprite("res/sprites/static/tree01.png", 16, 64, -1);
        TREE02 = new Sprite("res/sprites/static/tree02.png", 16, 64, -1);
        TREE03 = new Sprite("res/sprites/static/tree03.png", 32, 64, 6);
        TREE04 = new Sprite("res/sprites/static/tree04.png", 32, 32, -1);

        /** BUILDINGS **/
        BUILDING01 = new Sprite("res/sprites/static/building01.png", 64, 64, -1);
        BUILDING02 = new Sprite("res/sprites/static/building02.png", 96, 80, 4);

        /** FENCES **/
        FENCE01 = new Sprite("res/sprites/static/fence01.png", 16, 16, -1);
        FENCE02 = new Sprite("res/sprites/static/fence02.png", 16, 16, -1);
        FENCE03 = new Sprite("res/sprites/static/fence03.png", 16, 16, -1);
        FENCE04 = new Sprite("res/sprites/static/fence04.png", 16, 16, -1);

        /** LIGHTS **/
        LIGHT01 = new Sprite("res/sprites/static/light01.png", 16, 48, -1);
        TORCH01 = new Sprite("res/sprites/static/torch01.png", 16, 48, 3);

        /** TILESET **/
        TILESET = new Sprite("res/sprites/tiles/tileset.png", TileMap.TILE_WIDTH, TileMap.TILE_HEIGHT);

        /** SMOKE **/
        SMOKE01 = new Sprite("res/sprites/dynamic/smoke01-sheet.png", 8, 8, 6);

        /** INTERFACE **/
        IONIAN_ICON = new Sprite("res/sprites/interface/ionian_icon_16x16.png", 16, 16, -1);
        DORIAN_ICON = new Sprite("res/sprites/interface/dorian_icon_16x16.png", 16, 16, -1);
        PHRYGIAN_ICON = new Sprite("res/sprites/interface/phrygian_icon_16x16.png", 16, 16, -1);
        LYDIAN_ICON = new Sprite("res/sprites/interface/lydian_icon_16x16.png", 16, 16, -1);
        MIXOLYDIAN_ICON = new Sprite("res/sprites/interface/mixolydian_icon_16x16.png", 16, 16, -1);
        AEOLIAN_ICON = new Sprite("res/sprites/interface/aeolian_icon_16x16.png", 16, 16, -1);
        LOCRIAN_ICON = new Sprite("res/sprites/interface/locrian_icon_16x16.png", 16, 16, -1);

        HEALTH_BAR = new Sprite("res/sprites/interface/health_bar_64x8.png", 64, 8, -1);
        MANA_BAR = new Sprite("res/sprites/interface/mana_bar_64x8.png", 64, 8, -1);
        STAMINA_BAR = new Sprite("res/sprites/interface/stamina_bar_64x8.png", 64, 8, -1);
        EMPTY_BAR = new Sprite("res/sprites/interface/empty_bar_64x8.png", 64, 8, -1);

        HEALTH_POTION = new Sprite("res/sprites/interface/health_potion_8x8.png", 8, 8, -1);
        MANA_POTION = new Sprite("res/sprites/interface/mana_potion_8x8.png", 8, 8, -1);
        HASTE_POTION = new Sprite("res/sprites/interface/haste_potion_8x8.png", 8, 8, -1);

        GOLD_COIN_INTERFACE = new Sprite("res/sprites/interface/gold_coin_interface_8x8.png", 8, 8, -1);

        /** OTHERS **/
        GOLD_COIN = new Sprite("res/sprites/dynamic/gold_coin.png", 4, 3, -1);
        A_CONTROLLER_BUTTON = new Sprite("res/sprites/dynamic/a_controller_button_8x8.png", 8, 8, -1);
        F_KEYBOARD_KEY = new Sprite("res/sprites/dynamic/f_keyboard_key_8x8.png", 8, 8, -1);
    }

    public static SpriteManager getInstance() {
        if (instance == null) {
            instance = new SpriteManager();
        }
        return instance;
    }

    public static int numOfStaticEntitySprites = 12;

    public static Sprite getStaticEntitySprite(int i) {
        Sprite sprite = null;
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
                sprite = SpriteManager.getInstance().TREE04;
                break;
            case 4:
                sprite = SpriteManager.getInstance().BUILDING01;
                break;
            case 5:
                sprite = SpriteManager.getInstance().BUILDING02;
                break;
            case 6:
                sprite = SpriteManager.getInstance().FENCE01;
                break;
            case 7:
                sprite = SpriteManager.getInstance().FENCE02;
                break;
            case 8:
                sprite = SpriteManager.getInstance().FENCE03;
                break;
            case 9:
                sprite = SpriteManager.getInstance().FENCE04;
                break;
            case 10:
                sprite = SpriteManager.getInstance().LIGHT01;
                break;
            case 11:
                sprite = SpriteManager.getInstance().TORCH01;
                break;
            default:
                break;
        }
        return sprite;
    }
}
