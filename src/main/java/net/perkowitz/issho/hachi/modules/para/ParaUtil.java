package net.perkowitz.issho.hachi.modules.para;

import com.google.common.collect.Lists;
import com.google.common.collect.Maps;
import net.perkowitz.issho.devices.GridButton;
import net.perkowitz.issho.devices.GridControl;
import net.perkowitz.issho.devices.GridControlSet;
import net.perkowitz.issho.devices.GridPad;
import net.perkowitz.issho.devices.launchpadpro.Color;

import java.util.List;
import java.util.Map;

/**
 * Created by optic on 10/24/16.
 */
public class ParaUtil {

    /***** enums for various modes and settings *****************/
    public enum Gate {
        PLAY, TIE, REST
    }

    public enum StepEditState {
        // in button layout order
        NOTE, GATE, VELOCITY
//        MUTE, NOTE, VELOCITY, LENGTH, GATE, PLAY
    }

    public enum Function {
        SAVE, LOAD
    }

    public enum ValueState {
        STEP_OCTAVE, VELOCITY, KEYBOARD_OCTAVE, NONE
    }

    public enum View {
        SEQUENCE, SETTINGS
    }

    /***** locations of various controls on the grid ************************/
    public static int PATTERN_MIN_ROW = 0;
    public static int PATTERN_MAX_ROW = 1;
    public static int KEYBOARD_UPPER_BLACK = 2;
    public static int KEYBOARD_UPPER_WHITE = 3;
    public static int KEYBOARD_LOWER_BLACK = 4;
    public static int KEYBOARD_LOWER_WHITE = 5;
    public static int STEP_MIN_ROW = 6;
    public static int STEP_MAX_ROW = 7;

    // settings
    public static int SESSION_MIN_ROW = 0;
    public static int SESSION_MAX_ROW = 1;
    public static int FILE_LOAD_ROW = 2;
    public static int FILE_SAVE_ROW = 3;
    public static int MIDI_CHANNEL_MIN_ROW = 6;
    public static int MIDI_CHANNEL_MAX_ROW = 7;

    public static int LOWEST_OCTAVE = 1;

    public static GridControl SAVE_CONTROL = new GridControl(GridButton.at(GridButton.Side.Left, 7), 0);

    public static int STEP_CONTROL_SHIFT_LEFT_INDEX = 6;
    public static int STEP_CONTROL_SHIFT_RIGHT_INDEX = 7;


    /***** piano keyboard layout*******************************************************/

    public static List<GridControl> keyboardList = Lists.newArrayList();
    static {
        // index them by their note value (offset from C: 0..11) so we can find them easily
        keyboardList.add(new GridControl(GridPad.at(0, KEYBOARD_LOWER_WHITE), 0));
        keyboardList.add(new GridControl(GridPad.at(1, KEYBOARD_LOWER_BLACK), 1));
        keyboardList.add(new GridControl(GridPad.at(1, KEYBOARD_LOWER_WHITE), 2));
        keyboardList.add(new GridControl(GridPad.at(2, KEYBOARD_LOWER_BLACK), 3));
        keyboardList.add(new GridControl(GridPad.at(2, KEYBOARD_LOWER_WHITE), 4));
        keyboardList.add(new GridControl(GridPad.at(3, KEYBOARD_LOWER_WHITE), 5));
        keyboardList.add(new GridControl(GridPad.at(4, KEYBOARD_LOWER_BLACK), 6));
        keyboardList.add(new GridControl(GridPad.at(4, KEYBOARD_LOWER_WHITE), 7));
        keyboardList.add(new GridControl(GridPad.at(5, KEYBOARD_LOWER_BLACK), 8));
        keyboardList.add(new GridControl(GridPad.at(5, KEYBOARD_LOWER_WHITE), 9));
        keyboardList.add(new GridControl(GridPad.at(6, KEYBOARD_LOWER_BLACK), 10));
        keyboardList.add(new GridControl(GridPad.at(6, KEYBOARD_LOWER_WHITE), 11));
        keyboardList.add(new GridControl(GridPad.at(0, KEYBOARD_UPPER_WHITE), 0));
        keyboardList.add(new GridControl(GridPad.at(1, KEYBOARD_UPPER_BLACK), 1));
        keyboardList.add(new GridControl(GridPad.at(1, KEYBOARD_UPPER_WHITE), 2));
        keyboardList.add(new GridControl(GridPad.at(2, KEYBOARD_UPPER_BLACK), 3));
        keyboardList.add(new GridControl(GridPad.at(2, KEYBOARD_UPPER_WHITE), 4));
        keyboardList.add(new GridControl(GridPad.at(3, KEYBOARD_UPPER_WHITE), 5));
        keyboardList.add(new GridControl(GridPad.at(4, KEYBOARD_UPPER_BLACK), 6));
        keyboardList.add(new GridControl(GridPad.at(4, KEYBOARD_UPPER_WHITE), 7));
        keyboardList.add(new GridControl(GridPad.at(5, KEYBOARD_UPPER_BLACK), 8));
        keyboardList.add(new GridControl(GridPad.at(5, KEYBOARD_UPPER_WHITE), 9));
        keyboardList.add(new GridControl(GridPad.at(6, KEYBOARD_UPPER_BLACK), 10));
        keyboardList.add(new GridControl(GridPad.at(6, KEYBOARD_UPPER_WHITE), 11));
    }

    // map the keyboard index (0..15) to the note numbers as laid out on a piano octave
    public static Integer[] KEYBOARD_INDEX_TO_NOTE = new Integer[] {
            null, 1, 3, null, 6, 8, 10, null,
            0, 2, 4, 5, 7, 9, 11, 12
    };
    // map the octave note to index (0..15)
    public static Integer[] KEYBOARD_NOTE_TO_INDEX = new Integer[] {
            8, 1, 9, 2, 10, 11, 4, 12, 5, 13, 6, 14, 15
    };


    /***** control sets for each function group *****************************/

    // indices for functions
    public static int FUNCTION_SAVE_INDEX = 5;
//    public static int FUNCTION_LOAD_INDEX = 6;
    public static int FUNCTION_SETTINGS_INDEX = 6;
    public static int FUNCTION_MUTE_INDEX = 7;

    public static GridControlSet patternControls = GridControlSet.padRows(ParaUtil.PATTERN_MIN_ROW, ParaUtil.PATTERN_MAX_ROW);
    public static GridControlSet stepControls = GridControlSet.padRows(ParaUtil.STEP_MIN_ROW, ParaUtil.STEP_MAX_ROW);
    public static GridControlSet keyboardControls = new GridControlSet(ParaUtil.keyboardList);
    public static GridControlSet stepEditControls = GridControlSet.buttonSide(GridButton.Side.Bottom, 0, 8);
    public static GridControlSet valueControls = GridControlSet.buttonSideInverted(GridButton.Side.Right);
    public static GridControlSet functionControls = GridControlSet.buttonSide(GridButton.Side.Left, FUNCTION_SAVE_INDEX, FUNCTION_MUTE_INDEX);
    public static GridControl patternCopyControl = new GridControl(GridButton.at(GridButton.Side.Left, 2), 0);
    public static GridControl patternClearControl = new GridControl(GridButton.at(GridButton.Side.Left, 3), 0);

    // settings
    public static GridControlSet sessionControls = GridControlSet.padRows(ParaUtil.SESSION_MIN_ROW, ParaUtil.SESSION_MAX_ROW);
    public static GridControlSet loadControls = GridControlSet.padRows(ParaUtil.FILE_LOAD_ROW, ParaUtil.FILE_LOAD_ROW);
    public static GridControlSet saveControls = GridControlSet.padRows(ParaUtil.FILE_SAVE_ROW, ParaUtil.FILE_SAVE_ROW);
    public static GridControlSet midiChannelControls = GridControlSet.padRows(ParaUtil.MIDI_CHANNEL_MIN_ROW, ParaUtil.MIDI_CHANNEL_MAX_ROW);


    /***** color palettes **********************************************************/
    public static Integer COLOR_STEP_OFF = 0;
    public static Integer COLOR_STEP_PLAY = 1;
    public static Integer COLOR_STEP_TIE = 2;
    public static Integer COLOR_STEP_REST = 3;
    public static Integer COLOR_STEP_HIGHLIGHT = 4;

    public static Integer COLOR_KEYBOARD_WHITE_KEY = 10;
    public static Integer COLOR_KEYBOARD_BLACK_KEY = 11;
    public static Integer COLOR_KEYBOARD_HIGHLIGHT = 12;
    public static Integer COLOR_KEYBOARD_SELECTED = 13;

    public static Integer COLOR_MODE_INACTIVE = 20;
    public static Integer COLOR_MODE_ACTIVE = 21;
    public static Integer COLOR_VALUE_OFF = 22;
    public static Integer COLOR_VALUE_ON = 23;

    public static Integer COLOR_PATTERN = 30;
    public static Integer COLOR_PATTERN_SELECTED = 31;
    public static Integer COLOR_PATTERN_PLAYING = 32;
    public static Integer COLOR_PATTERN_CHAINED = 33;
    public static Integer COLOR_PATTERN_SELECTED_PLAYING = 34;
    public static Integer COLOR_PATTERN_EDIT = 35;
    public static Integer COLOR_PATTERN_EDIT_SELECTED = 36;

    public static Integer COLOR_SESSION = 40;
    public static Integer COLOR_SESSION_ACTIVE = 41;
    public static Integer COLOR_SESSION_NEXT = 42;

    public static Integer COLOR_FILE_LOAD = 50;
    public static Integer COLOR_FILE_SAVE = 51;
    public static Integer COLOR_FILE_ACTIVE = 52;

    public static Integer COLOR_MIDI_CHANNEL = 60;
    public static Integer COLOR_MIDI_CHANNEL_ACTIVE = 61;

    public static Map<Integer, Color> PALETTE = Maps.newHashMap();
    static {
        Color mainColor = Color.BRIGHT_YELLOW;
        Color mainColorDim = Color.DIM_YELLOW;
        Color selectColor = Color.WHITE;
        Color highlightColor = Color.BRIGHT_BLUE;
        PALETTE.put(COLOR_STEP_OFF, Color.OFF);
        PALETTE.put(COLOR_STEP_PLAY, mainColor);
        PALETTE.put(COLOR_STEP_TIE, mainColorDim);
        PALETTE.put(COLOR_STEP_REST, Color.OFF);
        PALETTE.put(COLOR_STEP_HIGHLIGHT, selectColor);
        PALETTE.put(COLOR_KEYBOARD_WHITE_KEY, Color.fromIndex(2));
        PALETTE.put(COLOR_KEYBOARD_BLACK_KEY, Color.DARK_GRAY);
        PALETTE.put(COLOR_KEYBOARD_HIGHLIGHT, mainColor);
        PALETTE.put(COLOR_KEYBOARD_SELECTED, selectColor);
        PALETTE.put(COLOR_MODE_INACTIVE, Color.DARK_GRAY);
        PALETTE.put(COLOR_MODE_ACTIVE, mainColor);
        PALETTE.put(COLOR_VALUE_OFF, Color.OFF);
        PALETTE.put(COLOR_VALUE_ON, mainColor);
        PALETTE.put(COLOR_PATTERN, mainColorDim);
        PALETTE.put(COLOR_PATTERN_SELECTED, selectColor);
        PALETTE.put(COLOR_PATTERN_PLAYING, selectColor);
        PALETTE.put(COLOR_PATTERN_CHAINED, Color.DARK_GRAY);
        PALETTE.put(COLOR_PATTERN_SELECTED_PLAYING, Color.DARK_GRAY);
        PALETTE.put(COLOR_PATTERN_EDIT, Color.DARK_GRAY);
        PALETTE.put(COLOR_PATTERN_EDIT_SELECTED, Color.DARK_GRAY);
        PALETTE.put(COLOR_SESSION, Color.BRIGHT_BLUE);
        PALETTE.put(COLOR_SESSION_ACTIVE, Color.WHITE);
        PALETTE.put(COLOR_SESSION_NEXT, Color.DARK_GRAY);
        PALETTE.put(COLOR_FILE_LOAD, Color.BRIGHT_GREEN);
        PALETTE.put(COLOR_FILE_SAVE, Color.BRIGHT_RED);
        PALETTE.put(COLOR_FILE_ACTIVE, Color.WHITE);
        PALETTE.put(COLOR_MIDI_CHANNEL, Color.DARK_GRAY);
        PALETTE.put(COLOR_MIDI_CHANNEL_ACTIVE, Color.WHITE);
    }

}
