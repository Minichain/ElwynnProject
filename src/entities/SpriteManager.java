package entities;

import scene.TileMap;

public class SpriteManager {
    private static SpriteManager instance = null;

    /** ENTITIES **/
    public Sprite PLAYER, SMOKE01, NOTCH, SARA, NPC01, NPC02,
            GHOST_IONIAN, GHOST_DORIAN, GHOST_PHRYGIAN, GHOST_LYDIAN, GHOST_MIXOLYDIAN, GHOST_AEOLIAN, GHOST_LOCRIAN,
            TREE01, TREE02, TREE03, TREE04,
            BUILDING01, BUILDING02,
            FENCE01, FENCE02, FENCE03, FENCE04,
            LIGHT01, TORCH01, UTILITY_POLE_01,
            WARP_UP_LARGE, WARP_DOWN_LARGE,
            G_CLEF, QUARTER_NOTE, EIGHTH_NOTE, DOUBLE_EIGHTH_NOTE,
            A_CONTROLLER_BUTTON,
            B_KEYBOARD_KEY, F_KEYBOARD_KEY, H_KEYBOARD_KEY, M_KEYBOARD_KEY;

    /** TILESET **/
    public Sprite TILESET;

    /** INTERFACE **/
    public Sprite IONIAN_ICON, DORIAN_ICON, PHRYGIAN_ICON, LYDIAN_ICON, MIXOLYDIAN_ICON, AEOLIAN_ICON, LOCRIAN_ICON,
            HEALTH_BAR, MANA_BAR, STAMINA_BAR, EMPTY_BAR;

    /** ITEMS **/
    public Sprite GOLD_COIN, HEALTH_POTION, MANA_POTION, HASTE_POTION, WOOD;

    public SpriteManager() {
        /** PLAYER **/
        PLAYER = new Sprite("res/sprites/dynamic/player01.png", 19, 20, 1, 8, 2, 1, 7, 3);

        /** NPC **/
        NOTCH = new Sprite("res/sprites/dynamic/notch.png", 19, 20, 1, 8, 2, 1, 7, 3);
        SARA = new Sprite("res/sprites/dynamic/sara.png", 19, 20, 1, 8, 2, 1, 7, 3);
        NPC01 = new Sprite("res/sprites/dynamic/npc01.png", 19, 20, 1, 8, 2, 1, 7, 3);
        NPC02 = new Sprite("res/sprites/dynamic/npc02.png", 19, 20, 1, 8, 2, 1, 7, 3);

        /** ENEMY **/
        GHOST_IONIAN = new Sprite("res/sprites/dynamic/ghost_enemy_ionian.png", 32, 32, 1, 4, 6, 1, 1, 1);
        GHOST_DORIAN = new Sprite("res/sprites/dynamic/ghost_enemy_dorian.png", 32, 32, 1, 4, 6, 1, 1, 1);
        GHOST_PHRYGIAN = new Sprite("res/sprites/dynamic/ghost_enemy_phrygian.png", 32, 32, 1, 4, 6, 1, 1, 1);
        GHOST_LYDIAN = new Sprite("res/sprites/dynamic/ghost_enemy_lydian.png", 32, 32, 1, 4, 6, 1, 1, 1);
        GHOST_MIXOLYDIAN = new Sprite("res/sprites/dynamic/ghost_enemy_mixolydian.png", 32, 32, 1, 4, 6, 1, 1, 1);
        GHOST_AEOLIAN = new Sprite("res/sprites/dynamic/ghost_enemy_aeolian.png", 32, 32, 1, 4, 6, 1, 1, 1);
        GHOST_LOCRIAN = new Sprite("res/sprites/dynamic/ghost_enemy_locrian.png", 32, 32, 1, 4, 6, 1, 1, 1);

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

        UTILITY_POLE_01 = new Sprite("res/sprites/static/utilityPole01.png", 16, 48, -1);

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

        A_CONTROLLER_BUTTON = new Sprite("res/sprites/interface/a_controller_button_8x8.png", 8, 8, -1);
        B_KEYBOARD_KEY = new Sprite("res/sprites/interface/b_keyboard_key_8x8.png", 8, 8, -1);
        F_KEYBOARD_KEY = new Sprite("res/sprites/interface/f_keyboard_key_8x8.png", 8, 8, -1);
        H_KEYBOARD_KEY = new Sprite("res/sprites/interface/h_keyboard_key_8x8.png", 8, 8, -1);
        M_KEYBOARD_KEY = new Sprite("res/sprites/interface/m_keyboard_key_8x8.png", 8, 8, -1);

        /** ITEMS **/
        HEALTH_POTION = new Sprite("res/sprites/interface/health_potion_8x8.png", 8, 8, -1);
        MANA_POTION = new Sprite("res/sprites/interface/mana_potion_8x8.png", 8, 8, -1);
        HASTE_POTION = new Sprite("res/sprites/interface/haste_potion_8x8.png", 8, 8, -1);
        GOLD_COIN = new Sprite("res/sprites/interface/gold_coin_8x8.png", 8, 8, -1);
        WOOD = new Sprite("res/sprites/interface/wood_8x8.png", 8, 8, -1);

        /** WARPS **/
        WARP_DOWN_LARGE = new Sprite("res/sprites/static/warp_down_32x32.png", 32, 32, -1);
        WARP_UP_LARGE = new Sprite("res/sprites/static/warp_up_32x32.png", 32, 32, -1);
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
