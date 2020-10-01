package main;

import audio.OpenALManager;
import database.DataBase;
import enums.Resolution;

public class Parameters {
    private static boolean debugMode = false;
    private static boolean spawnEnemies = false;
    private static float spawnRate = 1f;
    private static boolean shadersEnabled = true;

    /** GRAPHIC/DISPLAY SETTINGS **/
    private static int framesPerSecond;
    private static boolean fullScreen;
    private static Resolution resolution;
    private static float resolutionFactor;

    public static void init() {
        int dataBaseValue;

        /** FramesPerSecond **/
        dataBaseValue = DataBase.selectParameter("framesPerSecond");
        if (dataBaseValue != -1) {
            framesPerSecond = dataBaseValue;
        } else {
            setFramesPerSecond(60);
        }

        /** FullScreen **/
        dataBaseValue = DataBase.selectParameter("fullScreen");
        if (dataBaseValue != -1) {
            fullScreen = dataBaseValue != 0;
        } else {
            setFullScreen(true);
        }

        /** Resolution **/
        dataBaseValue = DataBase.selectParameter("resolution");
        if (dataBaseValue != -1) {
            resolution = Resolution.values()[dataBaseValue];
        } else {
            setResolution(Resolution.RESOLUTION_1920_1080);
        }
        resolutionFactor = (float) Parameters.getResolutionHeight() / (float) Resolution.RESOLUTION_1920_1080.getResolution()[1];

        /** MusicSoundLevel **/
        dataBaseValue = DataBase.selectParameter("musicSoundLevel");
        if (dataBaseValue > 0 && dataBaseValue < 100) {
            musicSoundLevel = dataBaseValue / 100f;
        } else {
            setMusicSoundLevel(0.5f);
        }

        /** EffectSoundLevel **/
        dataBaseValue = DataBase.selectParameter("effectSoundLevel");
        if (dataBaseValue > 0 && dataBaseValue < 100) {
            effectSoundLevel = dataBaseValue / 100f;
        } else {
            setEffectSoundLevel(0.5f);
        }

        /** AmbienceSoundLevel **/
        dataBaseValue = DataBase.selectParameter("ambienceSoundLevel");
        if (dataBaseValue > 0 && dataBaseValue < 100) {
            ambienceSoundLevel = dataBaseValue / 100f;
        } else {
            setAmbienceSoundLevel(0.5f);
        }
    }

    /**
     * Frames Per Second we would love to see our game to run at.
     */
    public static int getFramesPerSecond() {
        return framesPerSecond;
    }

    public static void setFramesPerSecond(int framesPerSecond) {
        Log.l("setFramesPerSecond to " + framesPerSecond);
        Parameters.framesPerSecond = framesPerSecond;
        DataBase.insertOrUpdateParameter("framesPerSecond", framesPerSecond);
    }

    public static int getResolutionWidth() {
        return resolution.getResolution()[0];
    }

    public static int getResolutionHeight() {
        return resolution.getResolution()[1];
    }

    public static void setResolution(Resolution resolution) {
        Log.l("setResolution to " + resolution);
        Parameters.resolution = resolution;
        DataBase.insertOrUpdateParameter("resolution", resolution.getResolutionValue());
        Parameters.resolutionFactor = (float) Parameters.getResolutionHeight() / (float) Resolution.RESOLUTION_1920_1080.getResolution()[1];
        Window.setWindowSize(resolution.getResolution()[0], resolution.getResolution()[1]);
    }

    public static float getResolutionFactor() {
        return resolutionFactor;
    }

    public static boolean isFullScreen() {
        return fullScreen;
    }

    public static void setFullScreen(boolean fullScreen) {
        Log.l("setFullScreen to " + fullScreen);
        Parameters.fullScreen = fullScreen;
        DataBase.insertOrUpdateParameter("fullScreen", fullScreen ? 1 : 0);
    }

    public static boolean isDebugMode() {
        return debugMode;
    }

    public static void setDebugMode(boolean debugMode) {
        Log.l("setDebugMode to " + debugMode);
        Parameters.debugMode = debugMode;
    }

    public static boolean isSpawnEnemies() {
        return spawnEnemies;
    }

    public static void setSpawnEnemies(boolean spawnEnemies) {
        Log.l("setSpawnEnemies to " + spawnEnemies);
        Parameters.spawnEnemies = spawnEnemies;
    }

    public static float getSpawnRate() {
        return spawnRate;
    }

    public static void setSpawnRate(float spawnRate) {
        Log.l("setSpawnRate to " + spawnRate);
        Parameters.spawnRate = spawnRate;
    }

    public static boolean isShadersEnabled() {
        return shadersEnabled;
    }

    public static void setShadersEnabled(boolean shadersEnabled) {
        Log.l("setShadersEnabled to " + shadersEnabled);
        Parameters.shadersEnabled = shadersEnabled;
    }

    /** AUDIO PARAMETERS **/
    private static float musicSoundLevel;
    private static float effectSoundLevel;
    private static float ambienceSoundLevel;

    public static float getMusicSoundLevel() {
        return musicSoundLevel;
    }

    public static void setMusicSoundLevel(float soundLevel) {
        Log.l("setMusicSoundLevel to " + soundLevel);
        OpenALManager.onMusicLevelChange(soundLevel);
        Parameters.musicSoundLevel = soundLevel;
        DataBase.insertOrUpdateParameter("musicSoundLevel", (int) (soundLevel * 100));
    }

    public static float getEffectSoundLevel() {
        return effectSoundLevel;
    }

    public static void setEffectSoundLevel(float soundLevel) {
        Log.l("setEffectSoundLevel to " + soundLevel);
        OpenALManager.onEffectLevelChange(soundLevel);
        Parameters.effectSoundLevel = soundLevel;
        DataBase.insertOrUpdateParameter("effectSoundLevel", (int) (soundLevel * 100));
    }

    public static float getAmbienceSoundLevel() {
        return ambienceSoundLevel;
    }

    public static void setAmbienceSoundLevel(float soundLevel) {
        Log.l("setAmbienceSoundLevel to " + soundLevel);
        OpenALManager.onAmbienceLevelChange(soundLevel);
        Parameters.ambienceSoundLevel = soundLevel;
        DataBase.insertOrUpdateParameter("ambienceSoundLevel", (int) (soundLevel * 100));
    }

    /** ------------------------ PROJECT VERSION ------------------------
     * [Major build number].[Minor build number].[Revision].[Package]
     * i.e. Version: 1.0.15.2
     * Major build number: This indicates a major milestone in the game, increment this when going from beta to release, from release to major updates.
     * Minor build number: Used for feature updates, large bug fixes etc.
     * Revision: Minor alterations on existing features, small bug fixes, etc.
     * Package: Your code stays the same, external library changes or asset file update.
     */
    private static String projectVersion = "0.00.06.0";

    public static String getProjectVersion() {
        return projectVersion;
    }
}
