package net.perkowitz.issho.controller.yaeltex;

import com.google.common.collect.Maps;
import net.perkowitz.issho.controller.Colors;

import java.awt.*;
import java.util.Map;

public class ColorModes {

    public enum Mode {
        DEFAULT, TWO_BIT, TWO_THREE_BIT
    }

    // The HachiXL uses an indexed color table. These are constants for common colors in the default palette.
    public static Map<Color, Integer> defaultMap = Maps.newHashMap();
    static {
        defaultMap.put(Colors.BLACK, 0);
        defaultMap.put(Colors.WHITE, 127);
        defaultMap.put(Colors.GRAY, 1);
        defaultMap.put(Colors.LIGHT_GRAY, 2);
        defaultMap.put(Colors.DARK_GRAY, 71);
        defaultMap.put(Colors.BRIGHT_GREEN, 40);
        defaultMap.put(Colors.DIM_GREEN, 44);
        defaultMap.put(Colors.BRIGHT_RED, 1);
        defaultMap.put(Colors.DIM_RED, 2);
        defaultMap.put(Colors.BRIGHT_ORANGE, 13);
        defaultMap.put(Colors.DIM_ORANGE, 14);
        defaultMap.put(Colors.BRIGHT_BLUE, 73);
        defaultMap.put(Colors.DIM_BLUE, 74);
        defaultMap.put(Colors.BRIGHT_CYAN, 67);
        defaultMap.put(Colors.DIM_CYAN, 69);
        defaultMap.put(Colors.BRIGHT_YELLOW, 22);
        defaultMap.put(Colors.DIM_YELLOW, 24);
        defaultMap.put(Colors.BRIGHT_PINK, 116);
        defaultMap.put(Colors.DIM_PINK, 117);
        defaultMap.put(Colors.BRIGHT_MAGENTA, 106);
        defaultMap.put(Colors.DIM_MAGENTA, 107);
        defaultMap.put(Colors.BRIGHT_PURPLE, 97);
        defaultMap.put(Colors.DIM_PURPLE, 98);
    }

    // This alternate map allots 2 bits to red and green, 3 to blue; these are constants for common colors.
    public static Map<Color, Integer> twoThreeBitMap = Maps.newHashMap();
    static {
        twoThreeBitMap.put(Colors.BLACK, 0b0000000);
        twoThreeBitMap.put(Colors.WHITE, 0b1111111);
        twoThreeBitMap.put(Colors.GRAY,  0b1010100);
        twoThreeBitMap.put(Colors.LIGHT_GRAY, 0b1111111);
        twoThreeBitMap.put(Colors.DARK_GRAY, 0b0101010);
        twoThreeBitMap.put(Colors.BRIGHT_GREEN, 0b0011000);
        twoThreeBitMap.put(Colors.DIM_GREEN, 0b0001000);
        twoThreeBitMap.put(Colors.BRIGHT_RED, 0b1100000);
        twoThreeBitMap.put(Colors.DIM_RED, 0b0100000);
        twoThreeBitMap.put(Colors.BRIGHT_ORANGE, 0b1110000);
        twoThreeBitMap.put(Colors.DIM_ORANGE, 0b1001000);
        twoThreeBitMap.put(Colors.BRIGHT_BLUE, 0b0000111);
        twoThreeBitMap.put(Colors.DIM_BLUE, 0b0000010);
        twoThreeBitMap.put(Colors.BRIGHT_CYAN, 0b0011110);
        twoThreeBitMap.put(Colors.DIM_CYAN, 0b0001010);
        twoThreeBitMap.put(Colors.BRIGHT_YELLOW, 0b1111000);
        twoThreeBitMap.put(Colors.DIM_YELLOW, 0b0101000);
        twoThreeBitMap.put(Colors.BRIGHT_PINK, 0b1110100);
        twoThreeBitMap.put(Colors.DIM_PINK, 0b1001010);
        twoThreeBitMap.put(Colors.BRIGHT_MAGENTA, 0b1100110);
        twoThreeBitMap.put(Colors.DIM_MAGENTA, 0b0100010);
        twoThreeBitMap.put(Colors.BRIGHT_PURPLE, 0b1000111);
        twoThreeBitMap.put(Colors.DIM_PURPLE, 0b0100100);
    }

    // This alternate map allots 2 bits to red, green, and blue; the upper colors are custom
    public static Map<Color, Integer> twoBitMap = Maps.newHashMap();
    static {
        twoBitMap.put(Colors.BLACK, 0);
        twoBitMap.put(Colors.DARK_GRAY, 97);
        twoBitMap.put(Colors.GRAY,  98);
        twoBitMap.put(Colors.LIGHT_GRAY, 99);
        twoBitMap.put(Colors.WHITE, 100);
        twoBitMap.put(Colors.BRIGHT_RED, 101);
        twoBitMap.put(Colors.DIM_RED, 102);
        twoBitMap.put(Colors.BRIGHT_ORANGE, 103);
        twoBitMap.put(Colors.DIM_ORANGE, 104);
        twoBitMap.put(Colors.BRIGHT_YELLOW, 105);
        twoBitMap.put(Colors.DIM_YELLOW, 106);
        twoBitMap.put(Colors.BRIGHT_GREEN, 107);
        twoBitMap.put(Colors.DIM_GREEN, 108);
        twoBitMap.put(Colors.BRIGHT_CYAN, 109);
        twoBitMap.put(Colors.DIM_CYAN, 110);
        twoBitMap.put(Colors.BRIGHT_BLUE, 111);
        twoBitMap.put(Colors.DIM_BLUE, 112);
        twoBitMap.put(Colors.BRIGHT_PURPLE, 113);
        twoBitMap.put(Colors.DIM_PURPLE, 114);
        twoBitMap.put(Colors.BRIGHT_MAGENTA, 115);
        twoBitMap.put(Colors.DIM_MAGENTA, 116);
        twoBitMap.put(Colors.BRIGHT_PINK, 117);
        twoBitMap.put(Colors.DIM_PINK, 118);
        twoBitMap.put(Colors.LIGHT_BLUE, 119);
        twoBitMap.put(Colors.LIGHT_GREEN, 120);
        twoBitMap.put(Colors.BRIGHT_BLUEGRAY, 121);
        twoBitMap.put(Colors.DIM_BLUEGRAY, 122);
        twoBitMap.put(Colors.SKY_BLUE, 123);
        twoBitMap.put(Colors.DIM_SKY_BLUE, 124);
    }

}
