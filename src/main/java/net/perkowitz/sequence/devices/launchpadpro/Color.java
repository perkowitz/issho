package net.perkowitz.sequence.devices.launchpadpro;

import lombok.Getter;

/**
 * Created by optic on 9/4/16.
 */
public class Color {

    public static Color OFF = Color.fromIndex(0);
    public static Color WHITE = Color.fromIndex(3);
    public static Color MED_GRAY = Color.fromIndex(1);
    public static Color LIGHT_GRAY = Color.fromIndex(2);
    public static Color DARK_GRAY = Color.fromIndex(71);
    public static Color BRIGHT_GREEN = Color.fromIndex(21);
    public static Color DIM_GREEN = Color.fromIndex(64);
    public static Color BRIGHT_RED = Color.fromIndex(5);
    public static Color DIM_RED = Color.fromIndex(7);
    public static Color BRIGHT_ORANGE = Color.fromIndex(9);
    public static Color DIM_ORANGE = Color.fromIndex(11);
    public static Color LIGHT_BLUE = Color.fromIndex(37);
    public static Color BRIGHT_BLUE = Color.fromIndex(41);
    public static Color DIM_BLUE = Color.fromIndex(43);
    public static Color DARK_BLUE = Color.fromIndex(47);
    public static Color BRIGHT_YELLOW = Color.fromIndex(13);
    public static Color DIM_YELLOW = Color.fromIndex(15);
    public static Color DIM_BLUEGRAY = Color.fromIndex(103);


    public static int[] grays = new int[] { 1, 2, 3, 70, 71};
    public static int[] reds = new int[] { 4, 5, 6, 7, 60, 72, 84, 106, 120, 121 };
    public static int[] oranges = new int[] { 8, 9, 10, 11, 61, 96, 126, 127 };
    public static int[] yellows = new int[] { 12, 13, 14, 15, 62, 74, 85, 97, 98, 99, 124, 125 };
    public static int[] greens = new int[] { 16, 17, 18, 19, 20, 21, 22, 23, 24, 25, 26, 27, 63, 64, 65, 75, 76, 77, 86, 87, 88, 101, 102, 122, 123 };
    public static int[] blues = new int[] { 36, 37, 38, 39, 40, 41, 42, 43, 44, 45, 46, 47, 66, 67, 68, 78, 79, 91, 92, 103, 104, 112, 115 };

    @Getter private int index;

    public Color(int index) {
        this.index = index;
    }


    /***** static methods ********************************/

    public static Color fromIndex(int index) {
        return new Color(index);
    }

    public static Color[] fromInts(int[] indices) {

        Color[] colors = new Color[indices.length];
        for (int i = 0; i < indices.length; i++) {
            colors[i] = fromIndex(indices[i]);
        }

        return colors;
    }

}
