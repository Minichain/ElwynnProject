package main;

public class GameMode {
    private static GameMode instance;
    private static Mode gameMode;
    private static CreativeMode creativeMode;

    public enum Mode {
        NORMAL, CREATIVE
    }

    public enum CreativeMode {
        FIRST_LAYER, SECOND_LAYER, THIRD_LAYER
    }

    private GameMode() {
        gameMode = Mode.NORMAL;
        creativeMode = CreativeMode.FIRST_LAYER;
    }

    public static GameMode getInstance() {
        if (instance == null) {
            instance = new GameMode();
        }
        return instance;
    }


    public Mode getGameMode() {
        return gameMode;
    }

    public void setGameMode(Mode gameMode) {
        GameMode.gameMode = gameMode;
    }

    public CreativeMode getCreativeMode() {
        return creativeMode;
    }

    public void setCreativeMode(CreativeMode creativeMode) {
        GameMode.creativeMode = creativeMode;
    }
}
