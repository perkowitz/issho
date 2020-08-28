// Colors provides a broader set of named instances of java.awt.Color for use with controllers.

package net.perkowitz.issho.controller;

import java.awt.*;

public class Colors {

    // OFF, DIM, and BRIGHT aren't really colors but can be used as a shorthand for monochrome controls.
    public static final Color OFF = Color.BLACK;
    public static final Color DIM = Color.GRAY;
    public static final Color BRIGHT = Color.WHITE;

    public static final Color BLACK = Color.BLACK;
    public static final Color WHITE = Color.WHITE;
    public static final Color GRAY = Color.GRAY;
    public static final Color DARK_GRAY = Color.DARK_GRAY;
    public static final Color LIGHT_GRAY = Color.LIGHT_GRAY;
    public static final Color BRIGHT_RED = Color.RED;
    public static final Color DIM_RED = half(BRIGHT_RED);
    public static final Color BRIGHT_ORANGE = Color.ORANGE;
    public static final Color DIM_ORANGE = half(BRIGHT_ORANGE);
    public static final Color BRIGHT_YELLOW = Color.YELLOW;
    public static final Color DIM_YELLOW = half(BRIGHT_YELLOW);
    public static final Color BRIGHT_GREEN = Color.GREEN;
    public static final Color DIM_GREEN = half(BRIGHT_GREEN);
    public static final Color BRIGHT_CYAN = Color.CYAN;
    public static final Color DIM_CYAN = half(BRIGHT_CYAN);
    public static final Color BRIGHT_BLUE = Color.BLUE;
    public static final Color DIM_BLUE = half(BRIGHT_BLUE);
    public static final Color BRIGHT_MAGENTA = Color.MAGENTA;
    public static final Color DIM_MAGENTA = half(BRIGHT_MAGENTA);
    public static final Color BRIGHT_PINK = Color.PINK;
    public static final Color DIM_PINK = half(BRIGHT_PINK);
    public static final Color BRIGHT_PURPLE = new Color(128,  0, 255);
    public static final Color DIM_PURPLE = half(BRIGHT_PURPLE);

    public static final Color[] rainbow = new Color[]{BRIGHT_RED, BRIGHT_ORANGE, BRIGHT_YELLOW, BRIGHT_GREEN, BRIGHT_CYAN, BRIGHT_BLUE, BRIGHT_PURPLE, BRIGHT_MAGENTA};

    private static Color half(Color color) {
        return new Color(color.getRed() / 2, color.getGreen() / 2, color.getBlue() / 2);
    }


}
