package net.perkowitz.issho.hachi.modules.seq;

import com.google.common.collect.Lists;
import com.google.common.collect.Maps;
import net.perkowitz.issho.devices.GridButton;
import net.perkowitz.issho.devices.GridControl;
import net.perkowitz.issho.devices.GridControlSet;
import net.perkowitz.issho.devices.GridPad;
import net.perkowitz.issho.devices.launchpadpro.Color;

import java.util.List;
import java.util.Map;

import static net.perkowitz.issho.devices.GridButton.Side.*;

/**
 * Created by optic on 10/24/16.
 */
public class SeqUtil {

    public static int SESSION_COUNT = 16;
    public static int PATTERN_COUNT = 16;
    public static int STEP_COUNT = 16;
    public static int BEAT_TRACK_COUNT = 16;
    public static int CONTROL_TRACK_COUNT = 16;
    public static int LONG_PRESS_IN_MILLIS = 250;

    public static float VALUE_FACTOR_127 = 128 / 7;

    public enum SeqMode {
        BEAT, MONO, PARA
    }
    public enum EditMode {
        GATE, CONTROL, PITCH, STEP, JUMP
    }

    public static int[] BEAT_TRACK_NOTES = new int[] { 49, 37, 39, 51, 42, 44, 46, 50,
            36, 38, 40, 41, 43, 45, 47, 48 };


    /***** controls *****************/

    // a GridControlSet containing all the buttons along the bottom
    public static GridControlSet valueControls = GridControlSet.buttonSide(Right);

    // GridControlSets for rows of pads
    public static GridControlSet patternPlayControls = GridControlSet.padRows(0,1);
    public static GridControlSet trackMuteControls = GridControlSet.padRows(2,3);
    public static GridControlSet trackSelectControls = GridControlSet.padRows(4,5);
    public static GridControlSet stepControls = GridControlSet.padRows(6, 7);
    public static GridControlSet octaveControls = GridControlSet.padRow(3);

    // GridControlSet to represent a little keyboard
    public static int KEYBOARD_BLACK_ROW = 4;
    public static int KEYBOARD_WHITE_ROW = 5;
    static List<GridControl> keys = Lists.newArrayList();
    static {
        // create the keyboard in ascending key order
        keys.add(new GridControl(GridPad.at(0, KEYBOARD_WHITE_ROW), 0));
        keys.add(new GridControl(GridPad.at(1, KEYBOARD_BLACK_ROW), 1));
        keys.add(new GridControl(GridPad.at(1, KEYBOARD_WHITE_ROW), 2));
        keys.add(new GridControl(GridPad.at(2, KEYBOARD_BLACK_ROW), 3));
        keys.add(new GridControl(GridPad.at(2, KEYBOARD_WHITE_ROW), 4));
        keys.add(new GridControl(GridPad.at(3, KEYBOARD_WHITE_ROW), 5));
        keys.add(new GridControl(GridPad.at(4, KEYBOARD_BLACK_ROW), 6));
        keys.add(new GridControl(GridPad.at(4, KEYBOARD_WHITE_ROW), 7));
        keys.add(new GridControl(GridPad.at(5, KEYBOARD_BLACK_ROW), 8));
        keys.add(new GridControl(GridPad.at(5, KEYBOARD_WHITE_ROW), 9));
        keys.add(new GridControl(GridPad.at(6, KEYBOARD_BLACK_ROW), 10));
        keys.add(new GridControl(GridPad.at(6, KEYBOARD_WHITE_ROW), 11));
    }
    public static GridControlSet keyboardControls = new GridControlSet(keys);
    public static GridControlSet keyboardBackgroundControls = GridControlSet.padRows(KEYBOARD_BLACK_ROW, KEYBOARD_WHITE_ROW);

    // modifiers & step controls
    public static GridControl stepRestControl = new GridControl(GridPad.at(7, KEYBOARD_BLACK_ROW), 0);
    public static GridControl stepTieControl = new GridControl(GridPad.at(7, KEYBOARD_WHITE_ROW), 0);

    // left controls
    public static GridControl muteControl = new GridControl(GridButton.at(Left, 7), null);
    public static GridControl settingsControl = new GridControl(GridButton.at(Left, 6), null);
    public static GridControl saveControl = new GridControl(GridButton.at(Left, 5), null);
    public static GridControl copyControl = new GridControl(GridButton.at(Left, 2), null);
    public static GridControl patternSelectControl = new GridControl(GridButton.at(Left, 3), null);

    // edit mode controls
    public static GridControlSet editModeControls = GridControlSet.buttonSide(Bottom, 0, 2);
    public static GridControl stepControl = new GridControl(GridButton.at(Bottom, 3), null);
    public static GridControl jumpControl = new GridControl(GridButton.at(Bottom, 6), null);
    public static GridControl fillControl = new GridControl(GridButton.at(Bottom, 7), null);


    /***** colors **********************************************************/

    // special modifier colors
    public static Color STEP_TIE_COLOR = Color.BRIGHT_PURPLE;
    public static Color STEP_REST_COLOR = Color.DIM_PURPLE;
    public static Color MOD_HOLD_COLOR = Color.BRIGHT_PURPLE;
    public static Color MOD_STUTTER_COLOR = Color.BRIGHT_PINK;
    public static Color MOD_NO_HOLD_COLOR = Color.DIM_PURPLE;
    public static Color MOD_SKIP_COLOR = Color.BRIGHT_RED;
    public static Color MOD_LEGATO_COLOR = Color.DIM_BLUE_GREEN;

    // give color indices some easy names to refer to
    public static Integer COLOR_OFF = 0;
    public static Integer COLOR_ON = 1;
    public static Integer COLOR_HIGHLIGHT = 2;
    public static Integer COLOR_HIGHLIGHT_MID = 3;
    public static Integer COLOR_HIGHLIGHT_DIM = 4;
    public static Integer COLOR_PATTERN = 10;
    public static Integer COLOR_PATTERN_PLAYING = 11;
    public static Integer COLOR_PATTERN_CHAINED = 12;
    public static Integer COLOR_PATTERN_SELECTION = 13;
    public static Integer COLOR_PATTERN_SELECTED = 14;
    public static Integer COLOR_PATTERN_NEXT = 15;
    public static Integer COLOR_TRACK = 20;
    public static Integer COLOR_TRACK_SELECTION = 21;
    public static Integer COLOR_TRACK_SELECTED = 22;
    public static Integer COLOR_TRACK_MUTED = 23;
    public static Integer COLOR_TRACK_PLAYING = 24;
    public static Integer COLOR_TRACK_PLAYING_MUTED = 25;
    public static Integer COLOR_STEP_REST = 30;
    public static Integer COLOR_STEP_PLAY = 31;
    public static Integer COLOR_STEP_TIE = 32;
    public static Integer COLOR_VALUE_OFF = 40;
    public static Integer COLOR_VALUE_ON = 41;
    public static Integer COLOR_VALUE_ACCENT = 42;
    public static Integer COLOR_KEY_BLACK = 50;
    public static Integer COLOR_KEY_WHITE = 51;

    public static Map<Integer, Color> createPalette(Color playColor) {
        Map<Integer, Color> palette = Maps.newHashMap();
        Color playColorDim = Color.DARK_GRAY;
        Color selectColor = Color.fromIndex(1);
        Color highlightColor = Color.BRIGHT_YELLOW;
        Color highlightColorMid = Color.fromIndex(Color.yellows[2]);
        Color highlightColorDim = Color.DIM_YELLOW;
        if (playColor == Color.BRIGHT_YELLOW) {
            highlightColor = Color.BRIGHT_PURPLE;
            highlightColorDim = Color.DIM_PURPLE;
        }
        palette.put(COLOR_OFF, playColorDim);
        palette.put(COLOR_ON, Color.WHITE);
        palette.put(COLOR_HIGHLIGHT, highlightColor);
        palette.put(COLOR_HIGHLIGHT_MID, highlightColorMid);
        palette.put(COLOR_HIGHLIGHT_DIM, highlightColorDim);
        palette.put(COLOR_PATTERN, playColor);
        palette.put(COLOR_PATTERN_PLAYING, Color.WHITE);
        palette.put(COLOR_PATTERN_CHAINED, playColorDim);
        palette.put(COLOR_PATTERN_SELECTION, selectColor);
        palette.put(COLOR_PATTERN_SELECTED, highlightColor);
        palette.put(COLOR_PATTERN_NEXT, highlightColorDim);
        palette.put(COLOR_TRACK, selectColor);
        palette.put(COLOR_TRACK_SELECTION, playColor);
        palette.put(COLOR_TRACK_MUTED, Color.OFF);
        palette.put(COLOR_TRACK_SELECTED, Color.WHITE);
        palette.put(COLOR_TRACK_PLAYING, highlightColor);
        palette.put(COLOR_TRACK_PLAYING_MUTED, highlightColorDim);
        palette.put(COLOR_STEP_REST, Color.OFF);
        palette.put(COLOR_STEP_PLAY, Color.WHITE);
        palette.put(COLOR_STEP_TIE, Color.DARK_GRAY);
        palette.put(COLOR_VALUE_OFF, Color.OFF);
        palette.put(COLOR_VALUE_ON, playColor);
        palette.put(COLOR_VALUE_ACCENT, playColorDim);
        palette.put(COLOR_KEY_BLACK, Color.DARK_GRAY);
        palette.put(COLOR_KEY_WHITE, Color.LIGHT_GRAY);
        return palette;
    }

    public static Map<String, Color> paletteMap = Maps.newHashMap();
    static {
        // recommended palette colors
        paletteMap.put("pink", Color.DIM_PINK);
        paletteMap.put("blue", Color.BRIGHT_BLUE);
        paletteMap.put("green", Color.DIM_GREEN);
        paletteMap.put("red", Color.BRIGHT_RED);
        paletteMap.put("orange", Color.BRIGHT_ORANGE);
        paletteMap.put("purple", Color.BRIGHT_PURPLE);
        paletteMap.put("magenta", Color.BRIGHT_PINK_PURPLE);
        paletteMap.put("teal", Color.BRIGHT_BLUE_GREEN);
        paletteMap.put("yellow", Color.BRIGHT_YELLOW);
    }

    public static Map<Integer, Color> getPalette(String colorName) {
        Color color = paletteMap.get(colorName.toLowerCase());
        if (color == null) {
            color = Color.BRIGHT_BLUE;
        }
        return createPalette(color);
    }

    public static int valueToBase(int value) {
        return Math.min(Math.round(value / VALUE_FACTOR_127), 127);
    }

    public static int baseToValue(int base) {
        return Math.min(Math.round(base * VALUE_FACTOR_127), 127);
    }

}
