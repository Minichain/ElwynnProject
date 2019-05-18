package main;

import java.awt.*;
import java.awt.image.BufferedImage;

public class Utils {
    public enum DirectionFacing {
        LEFT, RIGHT, UP, DOWN;
    }

    public static double[] normalizeVector(double[] inputVector) {
        int vectorLength = inputVector.length;
        double[] vectorNormalized = new double[vectorLength];
        double vectorSum = 0;
        for (int i = 0; i < vectorLength; i++) {
            vectorSum += Math.pow(inputVector[i], 2);
        }
        vectorSum = Math.sqrt(vectorSum);
        for (int i = 0; i < vectorLength; i++) {
            vectorNormalized[i] = inputVector[i] / vectorSum;
        }
        return vectorNormalized;
    }

    public static DirectionFacing checkDirectionFacing(double[] displacementVector) {
        DirectionFacing directionFacing;
        displacementVector = Utils.normalizeVector(displacementVector);
        if (displacementVector[0] > 0) {
            if (displacementVector[1] > 0) {
                if (displacementVector[1] >= displacementVector[0]) {
                    directionFacing = DirectionFacing.DOWN;
                } else {
                    directionFacing = DirectionFacing.RIGHT;
                }
            } else {
                if (Math.abs(displacementVector[1]) >= displacementVector[0]) {
                    directionFacing = DirectionFacing.UP;
                } else {
                    directionFacing = DirectionFacing.RIGHT;
                }
            }
        } else {
            if (displacementVector[1] > 0) {
                if (displacementVector[1] >= Math.abs(displacementVector[0])) {
                    directionFacing = DirectionFacing.DOWN;
                } else {
                    directionFacing = DirectionFacing.LEFT;
                }
            } else {
                if (Math.abs(displacementVector[1]) >= Math.abs(displacementVector[0])) {
                    directionFacing = DirectionFacing.UP;
                } else {
                    directionFacing = DirectionFacing.LEFT;
                }
            }
        }
        return directionFacing;
    }

    public static BufferedImage resizeImage(BufferedImage img, int newW, int newH) {
        Image tmp = img.getScaledInstance(newW, newH, Image.SCALE_SMOOTH);
        BufferedImage resizedImage = new BufferedImage(newW, newH, BufferedImage.TYPE_INT_ARGB);

        Graphics2D g2d = resizedImage.createGraphics();
        g2d.drawImage(tmp, 0, 0, null);
        g2d.dispose();

        return resizedImage;
    }
}
