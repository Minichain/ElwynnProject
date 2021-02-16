package main;

public class GameTime {
    private static GameTime instance = null;
    private static float gameTime;  //InGame time in hours (0f - 24.0f)
    private static float gameTimeRealTimeFactor;
    private static float sunLightIntensity = 0.8f;
    private static float[] gameTimeLight;
    private static boolean slowMotion;

    private GameTime() {
        gameTime = 0f;
        gameTimeRealTimeFactor = 60f;   //60 times faster than in real life.
        slowMotion = false;
        gameTimeLight = new float[3];
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
//        Log.l("Updating ingame time. Current time: " + gameTime);
        updateGameTimeLight();
    }

    public static float getGameTime() {
        return gameTime;
    }

    public static void setGameTime(float gameTime) {
        gameTime = gameTime % 24f;
        Log.l("Game Time set to " + gameTime);
        GameTime.gameTime = gameTime;
    }

    /**
     * This function translates hours into light using "sigmoid" functions.
     */
    private static void updateGameTimeLight() {
        gameTimeLight = new float[]{getRedLight(), getGreenLight(), getBlueLight()};
    }

    public static float[] getGameTimeLight() {
        return gameTimeLight;
    }

    private static float getRedLight() {
        if (gameTime < 12.0) {
            return (1f / (1f + (float) Math.exp(-(gameTime - 8f) * 2f))) * sunLightIntensity;
        } else {
            return (1 - (1f / (1f + (float) Math.exp(-(gameTime - 20f) * 2f)))) * sunLightIntensity;
        }
    }

    private static float getGreenLight() {
        if (gameTime < 12.0) {
            return (1f / (1f + (float) Math.exp(-(gameTime - 8.25f) * 2))) * sunLightIntensity;
        } else {
            return (1 - (1f / (1f + (float) Math.exp(-(gameTime - 19.75f) * 2f)))) * sunLightIntensity;
        }
    }

    private static float getBlueLight() {
        if (gameTime < 12.0) {
            return (1f / (1f + (float) Math.exp(-(gameTime - 8.5f) * 2))) * sunLightIntensity;
        } else {
            return (1 - (1f / (1f + (float) Math.exp(-(gameTime - 19.5f) * 2f)))) * sunLightIntensity;
        }
    }

    public static float getGameTimeRealTimeFactor() {
        return gameTimeRealTimeFactor;
    }

    public static void setGameTimeRealTimeFactor(float gameTimeRealTimeFactor) {
        GameTime.gameTimeRealTimeFactor = gameTimeRealTimeFactor;
    }

    public static int getTimeSpeedFactor() {
        if (slowMotion) return 5;
        else return 1;
    }

    public static void setSlowMotion(boolean sm) {
        slowMotion = sm;
    }
}
