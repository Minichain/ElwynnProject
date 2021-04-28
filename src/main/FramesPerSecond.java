package main;

import utils.MathUtils;

/**
 * This class purpose is to update and show fps smoothly when displaying the "debug info".
 * We store the current fps in an array with a fixed size and we compute its mean when we desire
 * to know at how many fps our game is running.
 */
public class FramesPerSecond {
    private static int sizeOfArray = 10;

    private static float[] arrayOfFramesPerSecond = new float[sizeOfArray];
    private static int framesPerSecondIterator = 0;

    private static float[] arrayOfUpdatingTimeNanoseconds = new float[sizeOfArray];
    private static int updatingTimeIterator = 0;

    private static float[] arrayOfRenderingTimeNanoseconds = new float[sizeOfArray];
    private static int renderingTimeIterator = 0;

    private static float fps;

    public static void update(float fps) {
        arrayOfFramesPerSecond[framesPerSecondIterator] = fps;
        framesPerSecondIterator = (framesPerSecondIterator + 1) % arrayOfFramesPerSecond.length;
        FramesPerSecond.fps = MathUtils.computeMean(arrayOfFramesPerSecond);
    }

    public static void updateUpdatingTimeNanoseconds(long nanoseconds) {
        arrayOfUpdatingTimeNanoseconds[updatingTimeIterator] = nanoseconds;
        updatingTimeIterator = (updatingTimeIterator + 1) % arrayOfUpdatingTimeNanoseconds.length;
    }

    public static void updateRenderingTimeNanoseconds(long nanoseconds) {
        arrayOfRenderingTimeNanoseconds[renderingTimeIterator] = nanoseconds;
        renderingTimeIterator = (renderingTimeIterator + 1) % arrayOfRenderingTimeNanoseconds.length;
    }

    public static float getFramesPerSecond() {
        return fps;
    }

    public static float getUpdatingTime() {
        return MathUtils.computeMean(arrayOfUpdatingTimeNanoseconds);
    }

    public static float getRenderingTime() {
        return MathUtils.computeMean(arrayOfRenderingTimeNanoseconds);
    }
}
