package utils;

public class ArrayUtils {

    public static String toString(int[][] array) {
        StringBuilder s = new StringBuilder();
        for (int i = 0; i < array.length; i++) {
            for (int j = 0; j < array[0].length; j++) {
                s.append(String.format("%7d", array[i][j]));
                s.append(", ");
            }
            s.append("\n");
        }
        return s.toString();
    }

    public static String toString(boolean[][] array) {
        StringBuilder s = new StringBuilder();
        for (int i = 0; i < array.length; i++) {
            for (int j = 0; j < array[0].length; j++) {
                if (array[i][j]) s.append("1");
                else s.append("0");
                s.append(", ");
            }
            s.append("\n");
        }
        return s.toString();
    }

    public static String toString(Object[] array) {
        StringBuilder s = new StringBuilder();
        for (int i = 0; i < array.length; i++) {
            s.append(array[i]);
            s.append(", ");
        }
        return s.toString();
    }

    public static String toString(byte[] array) {
        StringBuilder s = new StringBuilder();
        for (int i = 0; i < array.length; i++) {
            s.append(array[i]);
            s.append(", ");
        }
        return s.toString();
    }

    public static boolean contains(int[] array, int target) {
        for (int i : array) {
            if (i == target) return true;
        }
        return false;
    }

    public static boolean contains(Object[] array, Object target) {
        for (Object i : array) {
            if (i.equals(target)) return true;
        }
        return false;
    }

    public static int compare(Object[] array1, Object[] array2) {
        int coincidences = 0;
        for (int i = 0; i < array1.length; i++) {
            if (array1[i].equals(array2[i])) coincidences++;
        }
        return coincidences;
    }
}
