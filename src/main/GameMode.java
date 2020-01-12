package main;

public class GameMode {
    private static GameMode instance;
    private static Mode gameMode;

    public enum Mode {
        NORMAL, CREATIVE
    }

    private GameMode() {
        gameMode = Mode.NORMAL;
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
}
