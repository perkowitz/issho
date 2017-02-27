package net.perkowitz.issho.hachi.modules.minibeat;

import com.google.common.collect.Maps;
import net.perkowitz.issho.devices.GridButton;
import net.perkowitz.issho.devices.GridControl;
import net.perkowitz.issho.devices.GridControlSet;
import net.perkowitz.issho.devices.GridPad;
import net.perkowitz.issho.devices.launchpadpro.Color;
import net.perkowitz.issho.hachi.modules.mono.MonoUtil;

import java.util.Map;

import static net.perkowitz.issho.devices.GridButton.Side.Bottom;
import static net.perkowitz.issho.devices.GridButton.Side.Left;
import static net.perkowitz.issho.devices.GridButton.Side.Right;

/**
 * Created by optic on 10/24/16.
 */
public class MinibeatUtil {

    public static int SESSION_COUNT = 16;
    public static int PATTERN_COUNT = 16;
    public static int TRACK_COUNT = 8;
    public static int STEP_COUNT = 16;


    /***** controls *****************/

    // a GridControlSet containing all the buttons along the bottom
    public static GridControlSet valueControls = GridControlSet.buttonSide(Right);

    // GridControlSets for rows of pads
    public static GridControlSet patternPlayControls = GridControlSet.padRows(0,1);
    public static GridControlSet patternSelectControls = GridControlSet.padRows(2,3);
    public static GridControlSet trackMuteControls = GridControlSet.padRow(4);
    public static GridControlSet trackSelectControls = GridControlSet.padRow(5);
    public static GridControlSet stepControls = GridControlSet.padRows(6, 7);

    // left controls
    public static GridControl muteControl = new GridControl(GridButton.at(Left, 7), null);
    public static GridControl settingsControl = new GridControl(GridButton.at(Left, 6), null);
    public static GridControl saveControl = new GridControl(GridButton.at(Left, 5), null);

    // standard settings controls
    public static GridControlSet sessionControls = GridControlSet.padRows(MonoUtil.SESSION_MIN_ROW, MonoUtil.SESSION_MAX_ROW);
    public static GridControlSet loadControls = GridControlSet.padRows(MonoUtil.FILE_LOAD_ROW, MonoUtil.FILE_LOAD_ROW);
    public static GridControlSet saveControls = GridControlSet.padRows(MonoUtil.FILE_SAVE_ROW, MonoUtil.FILE_SAVE_ROW);
    public static GridControlSet midiChannelControls = GridControlSet.padRows(MonoUtil.MIDI_CHANNEL_MIN_ROW, MonoUtil.MIDI_CHANNEL_MAX_ROW);


    /***** colors **********************************************************/

    // give color indices some easy names to refer to
    public static Integer COLOR_OFF = 0;
    public static Integer COLOR_ON = 1;
    public static Integer COLOR_PATTERN = 10;
    public static Integer COLOR_PATTERN_PLAYING = 11;
    public static Integer COLOR_PATTERN_CHAINED = 12;
    public static Integer COLOR_PATTERN_SELECTION = 13;
    public static Integer COLOR_PATTERN_SELECTED = 14;
    public static Integer COLOR_TRACK = 20;
    public static Integer COLOR_TRACK_SELECTION = 21;
    public static Integer COLOR_TRACK_SELECTED = 22;
    public static Integer COLOR_TRACK_MUTED = 23;
    public static Integer COLOR_TRACK_PLAYING = 24;
    public static Integer COLOR_TRACK_PLAYING_MUTED = 25;
    public static Integer COLOR_STEP_OFF = 30;
    public static Integer COLOR_STEP_ON = 31;

    // make a palette by setting colors for the named indices
    public static Map<Integer, Color> PALETTE = Maps.newHashMap();
    static {
        Color playColor = Color.DIM_GREEN;
        Color playColorDim = Color.DARK_GRAY;
        Color selectColor = Color.DIM_BLUE;
        PALETTE.put(COLOR_OFF, Color.DARK_GRAY);
        PALETTE.put(COLOR_ON, Color.WHITE);
        PALETTE.put(COLOR_PATTERN, playColor);
        PALETTE.put(COLOR_PATTERN_PLAYING, Color.WHITE);
        PALETTE.put(COLOR_PATTERN_CHAINED, playColorDim);
        PALETTE.put(COLOR_PATTERN_SELECTION, selectColor);
        PALETTE.put(COLOR_PATTERN_SELECTED, Color.WHITE);
        PALETTE.put(COLOR_TRACK, playColor);
        PALETTE.put(COLOR_TRACK_SELECTION, selectColor);
        PALETTE.put(COLOR_TRACK_MUTED, playColorDim);
        PALETTE.put(COLOR_TRACK_SELECTED, Color.WHITE);
        PALETTE.put(COLOR_TRACK_PLAYING, Color.BRIGHT_YELLOW);
        PALETTE.put(COLOR_TRACK_PLAYING_MUTED, Color.DIM_YELLOW);
        PALETTE.put(COLOR_STEP_OFF, Color.OFF);
        PALETTE.put(COLOR_STEP_ON, playColor);
    }

    // make another palette just for fun
    public static Map<Integer, Color> ANOTHER_PALETTE = Maps.newHashMap();
    static {
    }

}
