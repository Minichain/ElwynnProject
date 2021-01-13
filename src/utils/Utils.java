package utils;

import java.nio.ByteBuffer;

public class Utils {
    public enum DirectionFacing {
        LEFT, RIGHT, UP, DOWN;
    }

    public static DirectionFacing checkDirectionFacing(double[] movementVector) {
        movementVector = MathUtils.normalizeVector(movementVector);
        return checkDirectionFacing(movementVector[0], movementVector[1]);
    }

    public static DirectionFacing checkDirectionFacing(double x, double y) {
        DirectionFacing directionFacing;
        if (x > 0) {
            if (y > 0) {
                if (y > x) {
                    directionFacing = DirectionFacing.DOWN;
                } else {
                    directionFacing = DirectionFacing.RIGHT;
                }
            } else {
                if (Math.abs(y) > x) {
                    directionFacing = DirectionFacing.UP;
                } else {
                    directionFacing = DirectionFacing.RIGHT;
                }
            }
        } else {
            if (y > 0) {
                if (y > Math.abs(x)) {
                    directionFacing = DirectionFacing.DOWN;
                } else {
                    directionFacing = DirectionFacing.LEFT;
                }
            } else {
                if (Math.abs(y) > Math.abs(x)) {
                    directionFacing = DirectionFacing.UP;
                } else {
                    directionFacing = DirectionFacing.LEFT;
                }
            }
        }
        return directionFacing;
    }

    public static byte[] doubleToBytes(double value) {
        byte[] bytes = new byte[8];
        ByteBuffer.wrap(bytes).putDouble(value);
        return bytes;
    }

    public static double byteArrayToDouble(byte[] bytes) {
        return ByteBuffer.wrap(bytes).getDouble();
    }
}
