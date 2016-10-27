package net.perkowitz.issho.hachi.modules.mono;

import com.google.common.collect.Lists;
import net.perkowitz.issho.devices.launchpadpro.Color;

import java.util.List;

/**
 * Created by optic on 10/24/16.
 */
public class MonoUtil {

    public enum StepMode {
        HOLD, STUTTER, TIE, REST
    }

    public static int KEYBOARD_MIN_ROW = 4;
    public static int KEYBOARD_MAX_ROW = 5;

    public static int STEP_MIN_ROW = 6;
    public static int STEP_MAX_ROW = 7;

    // display color functions
    public static Integer COLOR_STEP_OFF = 0;
    public static Integer COLOR_STEP_ON = 1;
    public static Integer COLOR_STEP_SELECTED = 2;
    public static Integer COLOR_KEYBOARD_BLACK = 3;
    public static Integer COLOR_KEYBOARD_WHITE = 4;
    public static Integer COLOR_KEYBOARD_SELECTED = 5;


    public static List<Color> PALETTE_FUCHSIA = Lists.newArrayList(
            // 52-55 purple-pinks, 12-15 yellows
            Color.OFF, Color.fromIndex(52), Color.fromIndex(53),    // step
            Color.DARK_GRAY, Color.LIGHT_GRAY, Color.fromIndex(53)  // keyboard
    );

    // map the keyboard index (0..15) to the note numbers as laid out on a piano octave
    public static Integer[] KEYBOARD_INDEX_TO_NOTE = new Integer[] {
            null, 1, 3, null, 6, 8, 10,
            0, 2, 4, 5, 7, 9, 11, 12
    };
    // map the octave note to index (0..15)
    public static Integer[] KEYBOARD_NOTE_TO_INDEX = new Integer[] {
            8, 1, 9, 2, 10, 11, 4, 12, 5, 13, 6, 14, 15
    };





}
