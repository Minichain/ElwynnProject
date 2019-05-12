public class GameStatus {
    private static GameStatus instance = null;
    private boolean gameRunning;
    private boolean gamePaused;

    private GameStatus() {
    }

    public static GameStatus getInstance() {
        if (instance == null) {
            instance = new GameStatus();
        }
        return instance;
    }

    public void setGameRunning(boolean running) {
        gameRunning = running;
    }

    public boolean isGameRunning() {
        return gameRunning;
    }

    public void setGamePaused(boolean paused) {
        gamePaused = paused;
    }

    public boolean isGamePaused() {
        return gamePaused;
    }
}
