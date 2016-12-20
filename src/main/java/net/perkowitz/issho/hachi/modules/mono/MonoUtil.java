package net.perkowitz.issho.hachi.modules.mono;

import com.google.common.collect.Lists;
import net.perkowitz.issho.devices.GridButton;
import net.perkowitz.issho.devices.GridControl;
import net.perkowitz.issho.devices.GridControlSet;
import net.perkowitz.issho.devices.GridPad;
import net.perkowitz.issho.devices.launchpadpro.Color;

import java.util.List;

/**
 * Created by optic on 10/24/16.
 */
public class MonoUtil {

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
    public static int KEYBOARD_MIN_ROW = 4;
    public static int KEYBOARD_MAX_ROW = 5;
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
        keyboardList.add(new GridControl(GridPad.at(0, KEYBOARD_MAX_ROW), 0));
        keyboardList.add(new GridControl(GridPad.at(1, KEYBOARD_MIN_ROW), 1));
        keyboardList.add(new GridControl(GridPad.at(1, KEYBOARD_MAX_ROW), 2));
        keyboardList.add(new GridControl(GridPad.at(2, KEYBOARD_MIN_ROW), 3));
        keyboardList.add(new GridControl(GridPad.at(2, KEYBOARD_MAX_ROW), 4));
        keyboardList.add(new GridControl(GridPad.at(3, KEYBOARD_MAX_ROW), 5));
        keyboardList.add(new GridControl(GridPad.at(4, KEYBOARD_MIN_ROW), 6));
        keyboardList.add(new GridControl(GridPad.at(4, KEYBOARD_MAX_ROW), 7));
        keyboardList.add(new GridControl(GridPad.at(5, KEYBOARD_MIN_ROW), 8));
        keyboardList.add(new GridControl(GridPad.at(5, KEYBOARD_MAX_ROW), 9));
        keyboardList.add(new GridControl(GridPad.at(6, KEYBOARD_MIN_ROW), 10));
        keyboardList.add(new GridControl(GridPad.at(6, KEYBOARD_MAX_ROW), 11));
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

    public static GridControlSet patternControls = GridControlSet.padRows(MonoUtil.PATTERN_MIN_ROW, MonoUtil.PATTERN_MAX_ROW);
    public static GridControlSet stepControls = GridControlSet.padRows(MonoUtil.STEP_MIN_ROW, MonoUtil.STEP_MAX_ROW);
    public static GridControlSet keyboardControls = new GridControlSet(MonoUtil.keyboardList);
    public static GridControlSet stepEditControls = GridControlSet.buttonSide(GridButton.Side.Bottom, 0, 8);
    public static GridControlSet valueControls = GridControlSet.buttonSideInverted(GridButton.Side.Right);
    public static GridControlSet functionControls = GridControlSet.buttonSide(GridButton.Side.Left, FUNCTION_SAVE_INDEX, FUNCTION_MUTE_INDEX);
    public static GridControl patternCopyControl = new GridControl(GridPad.at(0,2), 0);
    public static GridControl patternClearControl = new GridControl(GridPad.at(1,2), 0);
//    public static GridControlSet patternEditControls = GridControlSet.pads(2, 2, 0, 1);

    // settings
    public static GridControlSet sessionControls = GridControlSet.padRows(MonoUtil.SESSION_MIN_ROW, MonoUtil.SESSION_MAX_ROW);
    public static GridControlSet loadControls = GridControlSet.padRows(MonoUtil.FILE_LOAD_ROW, MonoUtil.FILE_LOAD_ROW);
    public static GridControlSet saveControls = GridControlSet.padRows(MonoUtil.FILE_SAVE_ROW, MonoUtil.FILE_SAVE_ROW);
    public static GridControlSet midiChannelControls = GridControlSet.padRows(MonoUtil.MIDI_CHANNEL_MIN_ROW, MonoUtil.MIDI_CHANNEL_MAX_ROW);


    /***** color palettes **********************************************************/
    public static Integer COLOR_STEP_OFF = 0;
    public static Integer COLOR_STEP_PLAY = 1;
    public static Integer COLOR_STEP_TIE = 2;
    public static Integer COLOR_STEP_REST = 3;
    public static Integer COLOR_STEP_HIGHLIGHT = 4;
    public static Integer COLOR_KEYBOARD_KEY = 5;
    public static Integer COLOR_KEYBOARD_HIGHLIGHT = 6;
    public static Integer COLOR_KEYBOARD_SELECTED = 7;
    public static Integer COLOR_MODE_INACTIVE = 8;
    public static Integer COLOR_MODE_ACTIVE = 9;
    public static Integer COLOR_VALUE_OFF = 10;
    public static Integer COLOR_VALUE_ON = 11;
    public static Integer COLOR_PATTERN = 12;
    public static Integer COLOR_PATTERN_SELECTED = 13;
    public static Integer COLOR_PATTERN_PLAYING = 14;
    public static Integer COLOR_PATTERN_CHAINED = 15;
    public static Integer COLOR_PATTERN_SELECTED_PLAYING = 16;
    public static Integer COLOR_SESSION = 17;
    public static Integer COLOR_SESSION_ACTIVE = 18;
    public static Integer COLOR_SESSION_NEXT = 19;
    public static Integer COLOR_FILE_LOAD = 20;
    public static Integer COLOR_FILE_SAVE = 21;
    public static Integer COLOR_FILE_ACTIVE = 22;
    public static Integer COLOR_MIDI_CHANNEL = 23;
    public static Integer COLOR_MIDI_CHANNEL_ACTIVE = 24;
    public static Integer COLOR_PATTERN_EDIT = 25;
    public static Integer COLOR_PATTERN_EDIT_SELECTED = 26;

    public static List<Color> PALETTE_FUCHSIA = Lists.newArrayList(
            // 52-55 purple-pinks, 12-15 yellows, 40-43 blues
            Color.OFF, Color.fromIndex(53), Color.fromIndex(55), Color.OFF, Color.fromIndex(13),    // step
            Color.DARK_GRAY, Color.fromIndex(13), Color.WHITE,                                      // keyboard
            Color.DARK_GRAY, Color.fromIndex(55),                                                   // gate
            Color.OFF, Color.fromIndex(55),                                                         // value
            Color.fromIndex(43), Color.WHITE, Color.fromIndex(40), Color.DARK_GRAY, Color.WHITE,    // pattern
            Color.fromIndex(55), Color.WHITE, Color.DARK_GRAY,                                      // session
            Color.DIM_GREEN, Color.DIM_RED, Color.WHITE,                                            // file
            Color.fromIndex(55), Color.WHITE,                                                       // midi channel
            Color.DARK_GRAY, Color.WHITE                                                            // pattern edit
    );

    public static List<Color> PALETTE_ORANGE = Lists.newArrayList(
            // 8-11 oranges, 36-39 blues
            Color.OFF, Color.fromIndex(9), Color.fromIndex(11), Color.OFF, Color.fromIndex(16),    // step
            Color.DARK_GRAY, Color.fromIndex(17), Color.WHITE,                                      // keyboard
            Color.DARK_GRAY, Color.fromIndex(11),                                                   // gate
            Color.OFF, Color.fromIndex(11),                                                         // value
            Color.fromIndex(19), Color.WHITE, Color.fromIndex(16), Color.DARK_GRAY, Color.WHITE,    // pattern
            Color.fromIndex(10), Color.WHITE, Color.DARK_GRAY,                                      // session
            Color.DIM_GREEN, Color.DIM_RED, Color.WHITE,                                            // file
            Color.fromIndex(10), Color.WHITE,                                                       // midi channel
            Color.DARK_GRAY, Color.WHITE                                                            // pattern edit
    );





}
