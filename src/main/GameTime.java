package main;

public class GameTime {
    private static GameTime instance = null;
    private static float gameTime;  //InGame time in hours (0f - 24.0f)
    private static float gameTimeRealTimeFactor = 100f;

    private GameTime() {
        gameTime = 0f;
    }

    public static GameTime getInstance() {
        if (instance == null) {
            instance = new GameTime();
        }
        return instance;
    }

    public static void update(long timeElapsed) {
        if (GameStatus.getStatus() == GameStatus.Status.PAUSED) {
            return;
        }
        float hoursElapsed = (float) timeElapsed / (3600.0f * 24.0f);
        gameTime = (gameTime + hoursElapsed * gameTimeRealTimeFactor) % 24.0f;
        System.out.println("Updating ingame time. Current time: " + gameTime);
    }

    public static float getGameTime() {
        return gameTime;
    }

    public static float getLight() {
        if (gameTime < 12.0) {
            return 1f / (1f + (float) Math.exp(-(gameTime / 24f) * 10f + 5f));
        } else {
            return 1f / (1f + (float) Math.exp((gameTime / 24f) * 10f - 5f));
        }
    }
}
