// Colors provides a broader set of named instances of java.awt.Color for use with controllers.

package net.perkowitz.issho.controller;

import java.awt.Color;

public class Colors {

    // OFF, DIM, and BRIGHT aren't really colors but can be used as a shorthand for monochrome controls.
    public static final Color OFF = Color.BLACK;
    public static final Color DIM = Color.GRAY;
    public static final Color BRIGHT = Color.WHITE;

    // grays
    public static final Color BLACK = Color.BLACK;
    public static final Color WHITE = Color.WHITE;
    public static final Color GRAY = Color.GRAY;
    public static final Color DARK_GRAY = Color.DARK_GRAY;
    public static final Color LIGHT_GRAY = Color.LIGHT_GRAY;

    // primary & secondary
    public static final Color BRIGHT_RED = Color.RED;
    public static final Color DIM_RED = dark(BRIGHT_RED);
    public static final Color BRIGHT_YELLOW = Color.YELLOW;
    public static final Color DIM_YELLOW = dark(BRIGHT_YELLOW);
    public static final Color BRIGHT_GREEN = Color.GREEN;
    public static final Color DIM_GREEN = dark(BRIGHT_GREEN);
    public static final Color BRIGHT_CYAN = Color.CYAN;
    public static final Color DIM_CYAN = dark(BRIGHT_CYAN);
    public static final Color BRIGHT_BLUE = Color.BLUE;
    public static final Color DIM_BLUE = dark(BRIGHT_BLUE);
    public static final Color BRIGHT_MAGENTA = Color.MAGENTA;
    public static final Color DIM_MAGENTA = dark(BRIGHT_MAGENTA);

    // tertiary
    public static final Color BRIGHT_ORANGE = Color.ORANGE;
    public static final Color DIM_ORANGE = dark(BRIGHT_ORANGE);
    public static final Color BRIGHT_LIME = new Color(127,  255, 0);
    public static final Color DIM_LIME = dark(BRIGHT_LIME);
    public static final Color BRIGHT_SAGE = new Color(0,  255, 127);
    public static final Color DIM_SAGE = dark(BRIGHT_SAGE);
    public static final Color BRIGHT_SKY = new Color(0,  127, 255);
    public static final Color DIM_SKY = dark(BRIGHT_SKY);
    public static final Color BRIGHT_PURPLE = new Color(127,  0, 255);
    public static final Color DIM_PURPLE = dark(BRIGHT_PURPLE);
    public static final Color BRIGHT_CRIMSON = new Color(255,  0, 127);
    public static final Color DIM_CRIMSON = dark(BRIGHT_CRIMSON);

    public static final Color BRIGHT_PINK = Color.PINK;
    public static final Color DIM_PINK = dark(BRIGHT_PINK);
    public static final Color LIGHT_BLUE = new Color(200, 218, 255);
    public static final Color LIGHT_GREEN = new Color(180,  255, 160);
    public static final Color BRIGHT_BLUEGRAY = new Color(200, 206, 218);
    public static final Color DIM_BLUEGRAY = new Color(127, 135, 150);
    public static final Color SKY_BLUE = new Color(0, 159, 255);
    public static final Color DIM_SKY_BLUE = new Color(0, 90, 225);

    public static final Color[] standardPalette = new Color []{
            BLACK, DARK_GRAY, GRAY, LIGHT_GRAY, WHITE,
            DIM_RED, BRIGHT_RED, DIM_ORANGE, BRIGHT_ORANGE, DIM_YELLOW, BRIGHT_YELLOW, DIM_LIME, BRIGHT_LIME,
            DIM_GREEN, BRIGHT_GREEN, DIM_SAGE, BRIGHT_SAGE, DIM_CYAN, BRIGHT_CYAN, DIM_SKY, BRIGHT_SKY,
            DIM_BLUE, BRIGHT_BLUE, DIM_PURPLE, BRIGHT_PURPLE, DIM_MAGENTA, BRIGHT_MAGENTA, DIM_CRIMSON, BRIGHT_CRIMSON,
            DIM_PINK, BRIGHT_PINK,
            LIGHT_BLUE, LIGHT_GREEN, BRIGHT_BLUEGRAY, DIM_BLUEGRAY, SKY_BLUE, DIM_SKY_BLUE
    };

    public static final Color[] rainbow = new Color[]{BRIGHT_RED, BRIGHT_ORANGE, BRIGHT_YELLOW, BRIGHT_GREEN, BRIGHT_BLUE, BRIGHT_PURPLE, BRIGHT_MAGENTA};


    /***** public helper functions *****/

    public static Color randomFromPalette() {
        int i = (int)(Math.random() * standardPalette.length);
        return standardPalette[i];
    }


    /***** private implementation *****/

    private static Color average(Color color1, Color color2) {
        return new Color(
                (color1.getRed() + color2.getRed())/2,
                (color1.getGreen() + color2.getGreen())/2,
                (color1.getBlue() + color2.getBlue())/2
                );
    }

    private static Color dark(Color color) {
        return average(color, BLACK);
    }

    private static Color light(Color color) {
        return average(color, WHITE);
    }


}
