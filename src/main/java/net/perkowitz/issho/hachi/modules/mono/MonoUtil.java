package net.perkowitz.issho.hachi.modules.mono;

import com.google.common.collect.Lists;
import com.google.common.collect.Maps;
import net.perkowitz.issho.devices.GridButton;
import net.perkowitz.issho.devices.GridPad;
import net.perkowitz.issho.devices.launchpadpro.Color;
import net.perkowitz.issho.hachi.modules.rhythm.RhythmInterface;

import java.util.List;
import java.util.Map;

import static net.perkowitz.issho.hachi.modules.mono.MonoUtil.StepEditMode.*;

/**
 * Created by optic on 10/24/16.
 */
public class MonoUtil {

    public enum Gate {
        HOLD, STUTTER, TIE, REST
    }

    public enum StepEditMode {
        MUTE, NOTE, VELOCITY, LENGTH, GATE
    }

    public static int PATTERN_MIN_ROW = 0;
    public static int PATTERN_MAX_ROW = 1;
    public static int KEYBOARD_MIN_ROW = 4;
    public static int KEYBOARD_MAX_ROW = 5;
    public static int STEP_MIN_ROW = 6;
    public static int STEP_MAX_ROW = 7;

    // map the keyboard index (0..15) to the note numbers as laid out on a piano octave
    public static Integer[] KEYBOARD_INDEX_TO_NOTE = new Integer[] {
            null, 1, 3, null, 6, 8, 10, null,
            0, 2, 4, 5, 7, 9, 11, 12
    };
    // map the octave note to index (0..15)
    public static Integer[] KEYBOARD_NOTE_TO_INDEX = new Integer[] {
            8, 1, 9, 2, 10, 11, 4, 12, 5, 13, 6, 14, 15
    };

    /***** buttons and pads **********************************************************/
    public static Map<StepEditMode, GridButton> modeButtonMap = Maps.newHashMap();
    public static Map<StepEditMode, GridPad> modePadMap = Maps.newHashMap();
    static {
        modeButtonMap.put(MUTE, GridButton.at(GridButton.Side.Bottom, 0));
        modeButtonMap.put(NOTE, GridButton.at(GridButton.Side.Bottom, 1));
        modeButtonMap.put(VELOCITY, GridButton.at(GridButton.Side.Bottom, 2));
        modeButtonMap.put(LENGTH, GridButton.at(GridButton.Side.Bottom, 3));
        modeButtonMap.put(GATE, GridButton.at(GridButton.Side.Bottom, 4));
    }

    public static List<GridControl> keyboardControls = Lists.newArrayList();
    static {
        keyboardControls.add(new GridControl(GridPad.at(1, KEYBOARD_MIN_ROW)));
        keyboardControls.add(new GridControl(GridPad.at(2, KEYBOARD_MIN_ROW)));
        keyboardControls.add(new GridControl(GridPad.at(4, KEYBOARD_MIN_ROW)));
        keyboardControls.add(new GridControl(GridPad.at(5, KEYBOARD_MIN_ROW)));
        keyboardControls.add(new GridControl(GridPad.at(6, KEYBOARD_MIN_ROW)));
        for (int index = 0; index < 8; index++) {
            keyboardControls.add(new GridControl(GridPad.at(index, KEYBOARD_MAX_ROW)));
        }
    }


    /***** color palettes **********************************************************/
    // display color functions
    public static Integer COLOR_STEP_OFF = 0;
    public static Integer COLOR_STEP_ON = 1;
    public static Integer COLOR_STEP_SELECTED = 2;
    public static Integer COLOR_STEP_HIGHLIGHT = 3;
    public static Integer COLOR_KEYBOARD_BLACK = 4;
    public static Integer COLOR_KEYBOARD_WHITE = 5;
    public static Integer COLOR_KEYBOARD_SELECTED = 6;
    public static Integer COLOR_MODE_INACTIVE = 7;
    public static Integer COLOR_MODE_ACTIVE = 8;

    public static List<Color> PALETTE_FUCHSIA = Lists.newArrayList(
            // 52-55 purple-pinks, 12-15 yellows
            Color.OFF, Color.fromIndex(55), Color.WHITE, Color.fromIndex(13),   // step
            Color.DARK_GRAY, Color.LIGHT_GRAY, Color.fromIndex(53),             // keyboard
            Color.DARK_GRAY, Color.fromIndex(55)             // mode
    );





}
