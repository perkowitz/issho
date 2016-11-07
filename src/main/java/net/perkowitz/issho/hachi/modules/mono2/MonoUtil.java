package net.perkowitz.issho.hachi.modules.mono2;

import com.google.common.collect.Lists;
import net.perkowitz.issho.devices.GridButton;
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
        NOTE, GATE
//        MUTE, NOTE, VELOCITY, LENGTH, GATE, PLAY
    }

    public enum Function {
        SAVE, LOAD
    }

    public enum ValueState {
        STEP_OCTAVE, VELOCITY, KEYBOARD_OCTAVE, NONE
    }

    /***** locations of various controls on the grid ************************/
    public static int PATTERN_MIN_ROW = 0;
    public static int PATTERN_MAX_ROW = 1;
    public static int KEYBOARD_MIN_ROW = 4;
    public static int KEYBOARD_MAX_ROW = 5;
    public static int STEP_MIN_ROW = 6;
    public static int STEP_MAX_ROW = 7;

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
    public static GridControlSet patternControls = GridControlSet.padRows(MonoUtil.PATTERN_MIN_ROW, MonoUtil.PATTERN_MAX_ROW);
    public static GridControlSet stepControls = GridControlSet.padRows(MonoUtil.STEP_MIN_ROW, MonoUtil.STEP_MAX_ROW);
    public static GridControlSet keyboardControls = new GridControlSet(MonoUtil.keyboardList);
    public static GridControlSet stepEditControls = GridControlSet.buttonSide(GridButton.Side.Bottom, 0, 8);
    public static GridControlSet valueControls = GridControlSet.buttonSideInverted(GridButton.Side.Right);
    public static GridControlSet functionControls = GridControlSet.buttonSide(GridButton.Side.Left, 0, 1);


    /***** color palettes **********************************************************/
    // display color functions
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

    public static List<Color> PALETTE_FUCHSIA = Lists.newArrayList(
            // 52-55 purple-pinks, 12-15 yellows
            Color.OFF, Color.fromIndex(53), Color.fromIndex(55), Color.OFF, Color.fromIndex(13),   // step
            Color.DARK_GRAY, Color.fromIndex(13), Color.WHITE,             // keyboard
            Color.DARK_GRAY, Color.fromIndex(55),             // gate
            Color.OFF, Color.fromIndex(55)                    // value
    );





}
