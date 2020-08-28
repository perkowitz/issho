package net.perkowitz.issho.hachi.modules;

import lombok.Setter;
import net.perkowitz.issho.util.Terminal;
import net.perkowitz.issho.util.Terminal.Color;

import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;

import static net.perkowitz.issho.util.Terminal.Color.*;

public class TextDisplay {

    public static int TOP_ROW = 2;
    public static int BOTTOM_ROW = 12;
    public static int LEFT_COLUMN = 6;
    public static int RIGHT_COLUMN = 46;
    public static int COLUMN_WIDTH = 5;
    public static int LOG_ROW = 16;

    public static Color defaultColor = GREEN;

    @Setter private static boolean enabled = false;

    public static void clearFrame() {
        if (!enabled) return;
        for (int row = 1; row < LOG_ROW; row++) {
            Terminal.clearRow(row);
        }
    }

    public static void drawFrame() {
        if (!enabled) return;
        Terminal.fg(RED);
        Terminal.go(TOP_ROW + 1, LEFT_COLUMN);
        System.out.printf(" --------------------------------------- ");
        Terminal.go(BOTTOM_ROW, LEFT_COLUMN);
        System.out.printf(" --------------------------------------- ");
        for (int i = 0; i < 8; i++) {
            Terminal.go(TOP_ROW + i + 2, LEFT_COLUMN);
            System.out.printf("|");
            Terminal.go(TOP_ROW + i + 2, RIGHT_COLUMN);
            System.out.printf("|");
        }
        Terminal.fg(defaultColor);
    }

    public static void drawModules(Module[] modules, int currentIndex) {
        if (!enabled) return;
        Terminal.fg(WHITE);
        for (int i = 0; i < modules.length; i++) {
            Terminal.go(TOP_ROW, LEFT_COLUMN + (i * COLUMN_WIDTH) + 1);
            if (i == currentIndex) {
                Terminal.invert();
                System.out.printf("%s", modules[i].shortName());
                Terminal.reset();
            } else {
                System.out.printf("%s", modules[i].shortName());
            }
        }
        Terminal.fg(defaultColor);
    }

    // labels consists of 8 labels for left buttons, 8 for right, 8 for bottom
    public static void drawButtons(String[] labels) {
        if (!enabled) return;
        if (labels.length < 24) {
            return;
        }

        Terminal.fg(WHITE);
        for (int i = 0; i < 8; i++) {
            Terminal.go(TOP_ROW + i + 2, 1);
            System.out.printf("%s", labels[i]);
        }
        for (int i = 8; i < 16; i++) {
            Terminal.go(TOP_ROW + i - 6, RIGHT_COLUMN + 2);
            System.out.printf("%s", labels[i]);
        }
        for (int i = 16; i < 24; i++) {
            Terminal.go(BOTTOM_ROW + 1, LEFT_COLUMN + ((i - 16) * COLUMN_WIDTH) + 1);
            System.out.printf("%s ", labels[i]);
        }
        Terminal.fg(defaultColor);
    }

    public static void drawRows(String[] labels) {
        if (!enabled) return;
        if (labels.length < 8) {
            return;
        }

        Terminal.fg(WHITE);
        for (int i = 0; i < 8; i++) {
            Terminal.go(TOP_ROW + i + 2, LEFT_COLUMN + 4);
            System.out.printf("%s ", labels[i]);
        }
        Terminal.fg(defaultColor);
    }

    public static void drawTime(String beat) {
        if (!enabled) return;

        Terminal.fg(WHITE);
        DateTimeFormatter dtf = DateTimeFormatter.ofPattern("HH:mm:ss");
        LocalDateTime now = LocalDateTime.now();
        Terminal.go(TOP_ROW, RIGHT_COLUMN + 4);
        System.out.printf(dtf.format(now));
        Terminal.go(TOP_ROW + 1, RIGHT_COLUMN + 3);
        System.out.printf(beat);
        Terminal.fg(defaultColor);
        Terminal.go(1,1);

    }
}
