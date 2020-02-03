package main;

import java.awt.*;
import java.awt.image.BufferedImage;
import java.io.BufferedReader;
import java.io.FileInputStream;
import java.io.InputStreamReader;

public class Utils {
    public enum DirectionFacing {
        LEFT, RIGHT, UP, DOWN;
    }

    public static double[] normalizeVector(double[] inputVector) {
        int vectorLength = inputVector.length;
        double[] vectorNormalized = new double[vectorLength];
        double vectorModule = Utils.module(inputVector);
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

    public static DirectionFacing checkDirectionFacing(double[] displacementVector) {
        displacementVector = Utils.normalizeVector(displacementVector);
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

    public static BufferedImage resizeImage(BufferedImage img, int newW, int newH) {
        Image tmp = img.getScaledInstance(newW, newH, Image.SCALE_SMOOTH);
        BufferedImage resizedImage = new BufferedImage(newW, newH, BufferedImage.TYPE_INT_ARGB);

        Graphics2D g2d = resizedImage.createGraphics();
        g2d.drawImage(tmp, 0, 0, null);
        g2d.dispose();

        return resizedImage;
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

    public static String readFileAsString(String filename) throws Exception {
        StringBuilder source = new StringBuilder();

        FileInputStream in = new FileInputStream(filename);

        Exception exception = null;

        BufferedReader reader;
        try {
            reader = new BufferedReader(new InputStreamReader(in,"UTF-8"));

            Exception innerExc= null;
            try {
                String line;
                while ((line = reader.readLine()) != null) {
                    source.append(line).append('\n');
                }
            } catch (Exception e) {
                exception = e;
            } finally {
                try {
                    reader.close();
                }
                catch (Exception exc) {
                    if (innerExc == null) {
                        innerExc = exc;
                    } else {
                        exc.printStackTrace();
                    }
                }
            }

            if (innerExc != null) {
                throw innerExc;
            }
        } catch (Exception e) {
            exception = e;
        } finally {
            try {
                in.close();
            }
            catch (Exception e) {
                if (exception == null) {
                    exception = e;
                } else {
                    e.printStackTrace();
                }
            }

            if (exception != null) {
                throw exception;
            }
        }

        return source.toString();
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
}
