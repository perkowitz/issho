package net.perkowitz.issho.controller.apps.hachi;

import com.google.common.collect.Maps;
import net.perkowitz.issho.controller.Colors;

import java.awt.*;
import java.util.Map;

public class Palette {

    public String Name = "Default";
    public Color Key = Colors.SKY_BLUE;
    public Color KeyDim = Colors.DIM_BLUE;
    public Color Off = Colors.DARK_GRAY;
    public Color On = Colors.WHITE;
    public Color Accent = Colors.BRIGHT_YELLOW;
    public Color AccentDim = Colors.DIM_YELLOW;

    private static Map<String,Palette> lookup = Maps.newHashMap();


    public Palette(String name, Color key, Color keyDim, Color off, Color on, Color accent, Color accentDim) {
        this.Name = name;
        this.Key = key;
        this.KeyDim = keyDim;
        this.Off = off;
        this.On = on;
        this.Accent = accent;
        this.AccentDim = accentDim;
        lookup.put(name.toLowerCase(), this);
    }

    public Palette(String name, Color key, Color keyDim) {
        this(name, key, keyDim, Colors.DARK_GRAY, Colors.WHITE, Colors.BRIGHT_YELLOW, Colors.DIM_YELLOW);
    }



    /***** static helper functions *****/

    public static final Palette DEFAULT = new Palette("Default", Colors.BRIGHT_BLUE, Colors.DIM_BLUE,
            Colors.DARK_GRAY, Colors.WHITE, Colors.BRIGHT_YELLOW, Colors.DIM_YELLOW);

    public static final Palette RED = new Palette("Red", Colors.BRIGHT_RED, Colors.DIM_RED);
    public static final Palette ORANGE = new Palette("Orange", Colors.BRIGHT_ORANGE, Colors.DIM_ORANGE);
    public static final Palette YELLOW = new Palette("Yellow", Colors.BRIGHT_YELLOW, Colors.DIM_YELLOW,
            Colors.DARK_GRAY, Colors.WHITE, Colors.BRIGHT_GREEN, Colors.DIM_GREEN);
    public static final Palette GREEN = new Palette("Green", Colors.BRIGHT_GREEN, Colors.DIM_GREEN);
    public static final Palette CYAN = new Palette("Cyan", Colors.BRIGHT_CYAN, Colors.DIM_CYAN);
    public static final Palette BLUE = new Palette("Blue", Colors.BRIGHT_BLUE, Colors.DIM_BLUE);
    public static final Palette PURPLE = new Palette("Purple", Colors.BRIGHT_PURPLE, Colors.DIM_PURPLE);
    public static final Palette PINK = new Palette("Pink", Colors.BRIGHT_PINK, Colors.DIM_PINK);
    public static final Palette MAGENTA = new Palette("Magenta", Colors.BRIGHT_MAGENTA, Colors.DIM_MAGENTA);

    public static Palette fromName(String name) {
        return lookup.get(name.toLowerCase());
    }

}
