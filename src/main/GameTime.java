package main;

public class GameTime {
    private static GameTime instance = null;
    private static float gameTime;  //InGame time in hours (0f - 24.0f)
    private static float gameTimeRealTimeFactor = 60f;  //60 times faster than in real life.
//    private static float gameTimeRealTimeFactor = 1000f;
    private static float sunLightIntensity = 0.8f;

    private GameTime() {
        gameTime = 0f;
    }

    public static GameTime getInstance() {
        if (instance == null) {
            instance = new GameTime();
        }
        return instance;
    }

    public void update(long timeElapsed) {
        if (GameStatus.getStatus() == GameStatus.Status.PAUSED) {
            return;
        }
        float hoursElapsed = (float) timeElapsed / (1000.0f * 3600.0f);   //From milliseconds to hours
        gameTime = (gameTime + hoursElapsed * gameTimeRealTimeFactor) % 24.0f;
//        System.out.println("Updating ingame time. Current time: " + gameTime);
    }

    public static float getGameTime() {
        return gameTime;
    }

    /**
     * This function translates hours into light using "sigmoid" function.
     */
    public static float getLight() {
        if (gameTime < 12.0) {
            return (1f / (1f + (float) Math.exp(-(gameTime / 12f) * 10f + 5f))) * sunLightIntensity;
        } else {
            return (1f / (1f + (float) Math.exp(((gameTime - 12f) / 12f) * 10f - 5f))) * sunLightIntensity;
        }
    }

    public static float getGameTimeRealTimeFactor() {
        return gameTimeRealTimeFactor;
    }

    public static void setGameTimeRealTimeFactor(float gameTimeRealTimeFactor) {
        GameTime.gameTimeRealTimeFactor = gameTimeRealTimeFactor;
    }
}
