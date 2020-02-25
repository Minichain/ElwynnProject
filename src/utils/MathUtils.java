package utils;

import listeners.MyInputListener;
import main.Coordinates;

public class MathUtils {

    public static double[] normalizeVector(double[] inputVector) {
        int vectorLength = inputVector.length;
        double[] vectorNormalized = new double[vectorLength];
        double vectorModule = module(inputVector);
        if (vectorModule > 0) {
            for (int i = 0; i < vectorLength; i++) {
                vectorNormalized[i] = inputVector[i] / vectorModule;
            }
        } else {
            vectorNormalized = inputVector;
        }
        return vectorNormalized;
    }

    public static double[] generateOrthonormalVector(double[] v1) {
        double[] v2 = new double[]{1, 0};
        v2[1] = - (v1[0] * v2[0]) / v1[1];
        return v2;
    }

    public static double module(double[] vector) {
        double sum = 0;
        for (int i = 0; i < vector.length; i++) {
            sum += Math.pow(vector[i], 2);
        }
        return Math.sqrt(sum);
    }

    public static double module(Coordinates coordinates1, Coordinates coordinates2) {
        return Math.sqrt(Math.pow(coordinates1.x - coordinates2.x, 2)
                + Math.pow(coordinates1.y - coordinates2.y, 2));
    }

    public static boolean isPointInsideTriangle(int[] point, int[] vertex1, int[] vertex2, int[] vertex3) {
        return isPointInsideTriangle(
                new double[]{(double) point[0], (double) point[1]},
                new double[]{(double) vertex1[0], (double) vertex1[1]},
                new double[]{(double) vertex2[0], (double) vertex2[1]},
                new double[]{(double) vertex3[0], (double) vertex3[1]});
    }

    public static boolean isPointInsideTriangle(double[] point, double[] vertex1, double[] vertex2, double[] vertex3) {
        double d1, d2, d3;
        boolean has_neg, has_pos;

        d1 = sign(point, vertex1, vertex2);
        d2 = sign(point, vertex2, vertex3);
        d3 = sign(point, vertex3, vertex1);

        has_neg = (d1 < 0) || (d2 < 0) || (d3 < 0);
        has_pos = (d1 > 0) || (d2 > 0) || (d3 > 0);

        return !(has_neg && has_pos);
    }

    private static double sign(double[] vertex1, double[] vertex2, double[] vertex3) {
        return (vertex1[0] - vertex3[0]) * (vertex2[1] - vertex3[1]) - (vertex2[0] - vertex3[0]) * (vertex1[1] - vertex3[1]);
    }

    public static boolean isMouseInsideRectangle(int x, int y, int x2, int y2) {
        int[] mouseCameraCoordinates = MyInputListener.getMouseCameraCoordinates();
        return x < mouseCameraCoordinates[0] && mouseCameraCoordinates[0] < x2 && y < mouseCameraCoordinates[1] && mouseCameraCoordinates[1] < y2;
    }

    public static double[] rotateVector(double[] vector, double angle) {
        return new double[]{Math.cos(angle) * vector[0] - Math.sin(angle) * vector[1], Math.sin(angle) * vector[0] + Math.cos(angle) * vector[1]};
    }
}
