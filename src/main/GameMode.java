package main;

public class GameMode {
    private static Mode gameMode = Mode.NORMAL;
    private static CreativeMode creativeMode = CreativeMode.TILES;
    private static LayerEditing layerEditing = LayerEditing.FIRST_LAYER;

    public enum Mode {
        NORMAL, CREATIVE
    }

    public enum CreativeMode {
        TILES, STATIC_ENTITIES
    }

    public enum LayerEditing {
        FIRST_LAYER, SECOND_LAYER, THIRD_LAYER
    }

    public static Mode getGameMode() {
        return gameMode;
    }

    public static void setGameMode(Mode gameMode) {
        GameMode.gameMode = gameMode;
    }

    public static CreativeMode getCreativeMode() {
        return creativeMode;
    }

    public static void setCreativeMode(CreativeMode creativeMode) {
        if (getGameMode() == Mode.CREATIVE) {
            GameMode.creativeMode = creativeMode;
        }
    }

    public static LayerEditing getLayerEditing() {
        return layerEditing;
    }

    public static void setLayerEditing(LayerEditing layerEditing) {
        if (getGameMode() == Mode.CREATIVE && getCreativeMode() == CreativeMode.TILES) {
            GameMode.layerEditing = layerEditing;
        }
    }
}
