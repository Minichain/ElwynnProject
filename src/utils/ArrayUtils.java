package utils;

public class ArrayUtils {

    public static void print(int[][] array) {
        for (int i = 0; i < array.length; i++) {
            for (int j = 0; j < array[0].length; j++) {
                System.out.print(String.format("%7d", array[i][j]));
                System.out.print(", ");
            }
            System.out.print("\n");
        }
    }

    public static void print(boolean[][] array) {
        for (int i = 0; i < array.length; i++) {
            for (int j = 0; j < array[0].length; j++) {
                if (array[i][i]) System.out.print("1");
                else System.out.print("0");
                System.out.print(", ");
            }
            System.out.print("\n");
        }
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
