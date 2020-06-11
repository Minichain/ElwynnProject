package main;

import audio.OpenALManager;
import enums.Resolution;

public class Parameters {
    private static boolean debugMode = false;
    private static boolean spawnEnemies = false;
    private static float spawnRate = 1f;
    private static boolean shadersEnabled = true;

    /** GRAPHIC/DISPLAY SETTINGS **/
    private static int framesPerSecond = 60;
    private static boolean fullScreen = true;
    private static Resolution resolution = Resolution.RESOLUTION_1920_1080;
    private static float resolutionFactor = (float) Parameters.getResolutionHeight() / (float) Resolution.RESOLUTION_1920_1080.getResolution()[1];

    /**
     * Frames Per Second we would love to see our game to run at.
     */
    public static int getFramesPerSecond() {
        return framesPerSecond;
    }

    public static void setFramesPerSecond(int framesPerSecond) {
        Parameters.framesPerSecond = framesPerSecond;
    }

    public static int getResolutionWidth() {
        return resolution.getResolution()[0];
    }

    public static int getResolutionHeight() {
        return resolution.getResolution()[1];
    }

    public static void setResolution(Resolution resolution) {
        Parameters.resolution = resolution;
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
        Parameters.fullScreen = fullScreen;
    }

    public static boolean isDebugMode() {
        return debugMode;
    }

    public static void setDebugMode(boolean debugMode) {
        Parameters.debugMode = debugMode;
    }

    public static boolean isSpawnEnemies() {
        return spawnEnemies;
    }

    public static void setSpawnEnemies(boolean spawnEnemies) {
        Parameters.spawnEnemies = spawnEnemies;
    }

    public static float getSpawnRate() {
        return spawnRate;
    }

    public static void setSpawnRate(float spawnRate) {
        Parameters.spawnRate = spawnRate;
    }

    public static boolean isShadersEnabled() {
        return shadersEnabled;
    }

    public static void setShadersEnabled(boolean shadersEnabled) {
        Parameters.shadersEnabled = shadersEnabled;
    }

    /** AUDIO PARAMETERS **/
    private static float musicSoundLevel = 0f;
    private static float effectSoundLevel = 0f;
    private static float ambienceSoundLevel = 0f;

    public static float getMusicSoundLevel() {
        return musicSoundLevel;
    }

    public static void setMusicSoundLevel(float soundLevel) {
        OpenALManager.onMusicLevelChange(soundLevel);
        Parameters.musicSoundLevel = soundLevel;
    }

    public static float getEffectSoundLevel() {
        return effectSoundLevel;
    }

    public static void setEffectSoundLevel(float soundLevel) {
        OpenALManager.onEffectLevelChange(soundLevel);
        Parameters.effectSoundLevel = soundLevel;
    }

    public static float getAmbienceSoundLevel() {
        return ambienceSoundLevel;
    }

    public static void setAmbienceSoundLevel(float soundLevel) {
        OpenALManager.onAmbienceLevelChange(soundLevel);
        Parameters.ambienceSoundLevel = soundLevel;
    }

    /** ------------------------ PROJECT VERSION ------------------------
     * [Major build number].[Minor build number].[Revision].[Package]
     * i.e. Version: 1.0.15.2
     * Major build number: This indicates a major milestone in the game, increment this when going from beta to release, from release to major updates.
     * Minor build number: Used for feature updates, large bug fixes etc.
     * Revision: Minor alterations on existing features, small bug fixes, etc.
     * Package: Your code stays the same, external library changes or asset file update.
     */
    private static String projectVersion = "0.00.04.1";

    public static String getProjectVersion() {
        return projectVersion;
    }
}
