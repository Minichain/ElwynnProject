package utils;

import java.util.Arrays;

public class Utils {
    public enum DirectionFacing {
        LEFT, RIGHT, UP, DOWN;
    }

    public static DirectionFacing checkDirectionFacing(double[] displacementVector) {
        displacementVector = MathUtils.normalizeVector(displacementVector);
        return checkDirectionFacing(displacementVector[0], displacementVector[1]);
    }

    public static DirectionFacing checkDirectionFacing(double x, double y) {
        DirectionFacing directionFacing;
//        y = -y; // y axis inverted in libGdx
        if (x > 0) {
            if (y > 0) {
                if (y >= x) {
                    directionFacing = DirectionFacing.DOWN;
                } else {
                    directionFacing = DirectionFacing.RIGHT;
                }
            } else {
                if (Math.abs(y) >= x) {
                    directionFacing = DirectionFacing.UP;
                } else {
                    directionFacing = DirectionFacing.RIGHT;
                }
            }
        } else {
            if (y > 0) {
                if (y >= Math.abs(x)) {
                    directionFacing = DirectionFacing.DOWN;
                } else {
                    directionFacing = DirectionFacing.LEFT;
                }
            } else {
                if (Math.abs(y) >= Math.abs(x)) {
                    directionFacing = DirectionFacing.UP;
                } else {
                    directionFacing = DirectionFacing.LEFT;
                }
            }
        }
        return directionFacing;
    }

    public static void printArray(int[] array) {
        System.out.println(Arrays.toString(array));
    }

    public static void printArray(int[][] array) {
        for (int i = 0; i < array[0].length; i++) {
            printArray(array[i]);
        }
    }

    public static void printArray(boolean[] array) {
        System.out.println(Arrays.toString(array));
    }

    public static void printArray(boolean[][] array) {
        for (int i = 0; i < array[0].length; i++) {
            printArray(array[i]);
        }
    }
}
