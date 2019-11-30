package net.perkowitz.issho.util;

import com.google.common.collect.Maps;
import net.perkowitz.issho.devices.launchpadpro.Color;
import net.perkowitz.issho.devices.launchpadpro.LaunchpadPro;

import java.util.Map;

public class Terminal {

    public enum Color {
        BLACK, RED, GREEN, YELLOW, BLUE, MAGENTA, CYAN, WHITE
    }

    private static int FOREGROUND_BASE = 30;
    private static int BACKGROUND_BASE = 40;
    private static String BRIGHT_SUFFIX = ";1";

    public static void fg(Color color) {
        fg(color, false);
    }
    public static void fg(Color color, boolean bright) {
        String b = "";
        if (bright) {
            b = BRIGHT_SUFFIX;
        }
        System.out.printf("%c[%d%sm", 27, FOREGROUND_BASE + color.ordinal(), b);
    }

    public static void bg(Color color, boolean bright) {
        String b = "";
        if (bright) {
            b = BRIGHT_SUFFIX;
        }
        System.out.printf("%c[%d%sm", 27, BACKGROUND_BASE + color.ordinal(), b);
    }

    public static void reset() {
        System.out.printf("%c%s", 27, "[0m");
    }

    public static void go(int row, int column) {
        System.out.printf("%c[%d;%dH", 27, row, column);
    }

    public static void go(int row, int column, String message) {
        go(row, column);
        System.out.printf(message);
    }

    public static void go(int row, int column, Color color, String message) {
        go(row, column);
        fg(color);
        System.out.printf(message);
    }

    public static void invert() {
        System.out.printf("%c[7m", 27);
    }

    public static void clear() {
        System.out.printf("%c[%s", 27, "2J");
        go(1, 1);
    }

    public static void clearRow(int row) {
        go(row, 1);
        System.out.printf("%c[0K", 27);
    }

    public static void beginLine() {
        System.out.printf("%c[%nG", 27, 1);
    }
}
