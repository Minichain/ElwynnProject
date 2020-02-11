package main;

public class GameMode {
    private static Mode gameMode = Mode.NORMAL;
    private static CreativeMode creativeMode = CreativeMode.FIRST_LAYER;

    public enum Mode {
        NORMAL, CREATIVE
    }

    public enum CreativeMode {
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
        GameMode.creativeMode = creativeMode;
    }
}
