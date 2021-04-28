package main;

import audio.OpenALManager;
import database.DataBase;
import enums.Language;
import enums.Resolution;
import ui.UserInterface;

public class Parameters {
    private static boolean debugMode = false;
    private static boolean spawnEnemies = true;
    private static float spawnRate = 1f;
    private static boolean shadersEnabled = true;
    private static float renderDistance = 336f;
    private static float updateDistance = 2000f;
    private static Language language = Language.ENGLISH;

    /** GRAPHIC/DISPLAY SETTINGS **/
    private static int framesPerSecond;
    private static boolean fullScreen;
    private static Resolution resolution;
    private static float widthResolutionFactor;
    private static float heightResolutionFactor;

    public static void init() {
        int dataBaseValue;

        /** FramesPerSecond **/
        dataBaseValue = DataBase.selectParameter("framesPerSecond");
        if (dataBaseValue != -1) {
            setFramesPerSecond(dataBaseValue);
        } else {
            setFramesPerSecond(60);
        }

        /** FullScreen **/
        dataBaseValue = DataBase.selectParameter("fullScreen");
        if (dataBaseValue != -1) {
            setFullScreen(dataBaseValue != 0);
        } else {
            setFullScreen(true);
        }

        /** Resolution **/
        dataBaseValue = DataBase.selectParameter("resolution");
        if (dataBaseValue != -1) {
            setResolution(Resolution.values()[dataBaseValue]);
        } else {
            setResolution(Resolution.RESOLUTION_1920_1080);
        }

        /** Language **/
        dataBaseValue = DataBase.selectParameter("language");
        if (dataBaseValue != -1) {
            setLanguage(Language.values()[dataBaseValue]);
        } else {
            setLanguage(Language.ENGLISH);
        }

        /** MusicSoundLevel **/
        dataBaseValue = DataBase.selectParameter("musicSoundLevel");
        if (dataBaseValue > 0 && dataBaseValue < 100) {
            setMusicSoundLevel(dataBaseValue / 100f);
        } else {
            setMusicSoundLevel(0.5f);
        }

        /** EffectSoundLevel **/
        dataBaseValue = DataBase.selectParameter("effectSoundLevel");
        if (dataBaseValue > 0 && dataBaseValue < 100) {
            setEffectSoundLevel(dataBaseValue / 100f);
        } else {
            setEffectSoundLevel(0.5f);
        }

        /** AmbienceSoundLevel **/
        dataBaseValue = DataBase.selectParameter("ambienceSoundLevel");
        if (dataBaseValue > 0 && dataBaseValue < 100) {
            setAmbienceSoundLevel(dataBaseValue / 100f);
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
        if (framesPerSecond < 10) framesPerSecond = 10;
        else if (framesPerSecond > 250) framesPerSecond = 250;
        Log.l("setFramesPerSecond to " + framesPerSecond);
        Parameters.framesPerSecond = framesPerSecond;
        DataBase.insertOrUpdateParameter("framesPerSecond", framesPerSecond);
    }

    public static int getResolutionWidth() {
        return Parameters.resolution.getResolution()[0];
    }

    public static int getResolutionHeight() {
        return Parameters.resolution.getResolution()[1];
    }

    public static void setResolution(Resolution resolution) {
        Log.l("setResolution to " + resolution);
        Parameters.resolution = resolution;
        DataBase.insertOrUpdateParameter("resolution", resolution.getResolutionValue());
        Parameters.widthResolutionFactor = (float) resolution.getResolution()[0] / (float) Resolution.RESOLUTION_1920_1080.getResolution()[0];
        Parameters.heightResolutionFactor = (float) resolution.getResolution()[1] / (float) Resolution.RESOLUTION_1920_1080.getResolution()[1];
        UserInterface.onResolutionChanged();
    }

    public static float getWidthResolutionFactor() {
        return Parameters.widthResolutionFactor;
    }

    public static float getHeightResolutionFactor() {
        return Parameters.heightResolutionFactor;
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

    public static float getRenderDistance() {
        return Parameters.renderDistance;
    }

    public static void setRenderDistance(float renderDistance) {
        Log.l("setRenderDistance to " + renderDistance);
        Parameters.renderDistance = renderDistance;
    }

    public static float getUpdateDistance() {
        return Parameters.updateDistance;
    }

    public static void setUpdateDistance(float updateDistance) {
        Log.l("setUpdateDistance to " + updateDistance);
        Parameters.updateDistance = updateDistance;
    }

    public static Language getLanguage() {
        return Parameters.language;
    }

    public static void setLanguage(Language language) {
        Log.l("setLanguage to " + language);
        Parameters.language = language;
        DataBase.insertOrUpdateParameter("language", language.getValue());
        Strings.updateStrings();
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
    private static String projectVersion = "0.03.00.0";

    public static String getProjectVersion() {
        return projectVersion;
    }
}
