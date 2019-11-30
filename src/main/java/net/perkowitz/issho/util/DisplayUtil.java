package net.perkowitz.issho.util;

import static net.perkowitz.issho.util.DisplayUtil.Color.*;

public class DisplayUtil {

    // 2nd and 3rd rows should correspond: regular color/dim version, for ease of conversion; WHITE/GRAY should be start of lists
    public enum Color {
        BLACK,
        WHITE, RED, ORANGE, YELLOW, GREEN, BLUEGREEN, BLUE, PURPLE, PINK,
        GRAY, DIM_RED, DIM_ORANGE, DIM_YELLOW, DIM_GREEN, DIM_BLUEGREEN, DIM_BLUE, DIM_PURPLE, DIM_PINK
    }

    private static int WHITE_VALUE = WHITE.ordinal();
    private static int GRAY_VALUE = GRAY.ordinal();

    public static Color dim(Color color) {
        if (color == BLACK) {
            return BLACK;
        }
        if (color.ordinal() >= GRAY_VALUE) {
            return color;
        }

        int v = color.ordinal() + (GRAY_VALUE - WHITE_VALUE);
        return Color.values()[v];
    }

}

