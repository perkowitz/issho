package net.perkowitz.issho.controller.apps.hachi.modules.step;

import com.google.common.collect.Maps;
import net.perkowitz.issho.controller.Colors;
import net.perkowitz.issho.controller.elements.Button;
import net.perkowitz.issho.controller.elements.Element;
import net.perkowitz.issho.controller.elements.ElementSet;

import java.awt.*;
import java.util.Map;


/**
 * Created by optic on 10/24/16.
 */
public class StepUtil {

    // these should be <=4 chars
    public static String[] BUTTON_LABELS = new String[]{
            "Play", "Exit", "Copy", "Alt", "Mrkr", "Save", "Sett", "Mute",
            "-", "-", "-", "Patt", "-", "-", "-", "-",
            "Clr", "Note", "#/b", "Octv", "Velo", "Long", "Rpet", "Slid"
    };
    public static String[] BUTTON_LABELS_ALT = new String[]{
            "Play", "Exit", "Copy", "Alt", "Mrkr", "Save", "Sett", "Mute",
            "-", "-", "-", "Patt", "-", "-", "-", "-",
            "Left", "Rght", "Rand", "", "", "", "", ""
    };

    /***** controls *****************/

    public static final int MARKERS_GROUP = 2;
    public static final int PATTERNS_GROUP = 1;
    public static final int BUTTONS_GROUP = 0;
    public static final int PADS_GROUP = 0;

    public static ElementSet[] stageColumns = {
            ElementSet.pads(0, 0, 8, 0, 0),
            ElementSet.pads(0, 0, 8, 1, 1),
            ElementSet.pads(0, 0, 8, 2, 2),
            ElementSet.pads(0, 0, 8, 3, 3),
            ElementSet.pads(0, 0, 8, 4, 4),
            ElementSet.pads(0, 0, 8, 5, 5),
            ElementSet.pads(0, 0, 8, 6, 6),
            ElementSet.pads(0, 0, 8, 7, 7),
    };

    // markers
    public static ElementSet markerElements = null;
    public static Map<Element, Stage.Marker> markerPaletteMap = Maps.newHashMap();
    static {
        markerPaletteMap.put(Button.at(MARKERS_GROUP, 0), Stage.Marker.None);
        markerPaletteMap.put(Button.at(MARKERS_GROUP, 1), Stage.Marker.Note);
        markerPaletteMap.put(Button.at(MARKERS_GROUP, 2), Stage.Marker.Sharp);
        markerPaletteMap.put(Button.at(MARKERS_GROUP, 3), Stage.Marker.OctaveUp);
        markerPaletteMap.put(Button.at(MARKERS_GROUP, 4), Stage.Marker.VolumeUp);
        markerPaletteMap.put(Button.at(MARKERS_GROUP, 5), Stage.Marker.Longer);
        markerPaletteMap.put(Button.at(MARKERS_GROUP, 6), Stage.Marker.Repeat);
        markerPaletteMap.put(Button.at(MARKERS_GROUP, 7), Stage.Marker.Slide);
        markerElements = new ElementSet(markerPaletteMap.keySet());
    }
    public static ElementSet patternElements = ElementSet.buttons(PATTERNS_GROUP, 0, 7);

    // misc elements
    public static Element currentMarkerDisplayElement = Button.at(BUTTONS_GROUP, 4);
    public static Element altControlsElement = Button.at(BUTTONS_GROUP, 2);
    public static Element copyPatternElement = Button.at(BUTTONS_GROUP, 3);

    // alt elements (used in place of marker elements when alt is enabled)
    public static Element shiftLeftElement = Button.at(MARKERS_GROUP, 0);
    public static Element shiftRightElement = Button.at(MARKERS_GROUP, 1);
    public static Element randomOrderElement = Button.at(MARKERS_GROUP, 2);


    /***** colors *****/

    public static Map<Stage.Marker, Color> MARKER_COLORS = Maps.newHashMap();
    static {
        MARKER_COLORS.put(Stage.Marker.None, Colors.OFF);
        MARKER_COLORS.put(Stage.Marker.Note, Colors.SKY_BLUE);
        MARKER_COLORS.put(Stage.Marker.Sharp, Colors.BRIGHT_CYAN);
        MARKER_COLORS.put(Stage.Marker.Flat, Colors.DIM_CYAN);
        MARKER_COLORS.put(Stage.Marker.OctaveUp, Colors.BRIGHT_ORANGE);
        MARKER_COLORS.put(Stage.Marker.OctaveDown, Colors.DIM_ORANGE);
        MARKER_COLORS.put(Stage.Marker.VolumeUp, Colors.BRIGHT_GREEN);
        MARKER_COLORS.put(Stage.Marker.VolumeDown, Colors.DIM_GREEN);
        MARKER_COLORS.put(Stage.Marker.Longer, Colors.DIM_BLUE);
        MARKER_COLORS.put(Stage.Marker.Repeat, Colors.BRIGHT_MAGENTA);
        MARKER_COLORS.put(Stage.Marker.Skip, Colors.DIM_RED);
        MARKER_COLORS.put(Stage.Marker.Slide, Colors.DARK_GRAY);
        MARKER_COLORS.put(Stage.Marker.Tie, Colors.BRIGHT_PINK);
        MARKER_COLORS.put(Stage.Marker.Random, Colors.BRIGHT_YELLOW);
    }

    public static Color ACTIVE_NOTE_COLOR = Colors.WHITE;

}
