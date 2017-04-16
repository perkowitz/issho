package net.perkowitz.issho.hachi.modules.beatbox;

import com.google.common.collect.Maps;
import net.perkowitz.issho.devices.GridButton;
import net.perkowitz.issho.devices.GridControl;
import net.perkowitz.issho.devices.GridControlSet;
import net.perkowitz.issho.devices.launchpadpro.Color;

import java.util.Map;

import static net.perkowitz.issho.devices.GridButton.Side.*;

/**
 * Created by optic on 10/24/16.
 */
public class BeatUtil {

    public static int SESSION_COUNT = 16;
    public static int PATTERN_COUNT = 16;
    public static int TRACK_COUNT = 16;
    public static int STEP_COUNT = 16;


    public enum EditMode {
        ENABLE, VELOCITY, JUMP, PLAY
    }


    /***** controls *****************/

    // a GridControlSet containing all the buttons along the bottom
    public static GridControlSet valueControls = GridControlSet.buttonSide(Right);

    // GridControlSets for rows of pads
    public static GridControlSet patternPlayControls = GridControlSet.padRows(0,1);
    public static GridControlSet trackMuteControls = GridControlSet.padRows(2,3);
    public static GridControlSet trackSelectControls = GridControlSet.padRows(4,5);
    public static GridControlSet stepControls = GridControlSet.padRows(6, 7);

    // left controls
    public static GridControl muteControl = new GridControl(GridButton.at(Left, 7), null);
    public static GridControl settingsControl = new GridControl(GridButton.at(Left, 6), null);
    public static GridControl saveControl = new GridControl(GridButton.at(Left, 5), null);
    public static GridControl copyControl = new GridControl(GridButton.at(Left, 2), null);
    public static GridControl patternSelectControl = new GridControl(GridButton.at(Left, 3), null);

    // edit mode controls
    public static GridControlSet editModeControls = GridControlSet.buttonSide(Bottom, 0, 3);


    /***** colors **********************************************************/

    // give color indices some easy names to refer to
    public static Integer COLOR_OFF = 0;
    public static Integer COLOR_ON = 1;
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
    public static Integer COLOR_STEP_OFF = 30;
    public static Integer COLOR_STEP_ON = 31;
    public static Integer COLOR_VALUE_OFF = 40;
    public static Integer COLOR_VALUE_ON = 41;

    public static Map<Integer, Color> PALETTE_PINK = Maps.newHashMap();
    static {
        Color playColor = Color.DIM_PINK;
        Color playColorDim = Color.DARK_GRAY;
        Color selectColor = Color.fromIndex(1);
        Color highlightColor = Color.BRIGHT_YELLOW;
        PALETTE_PINK.put(COLOR_OFF, playColorDim);
        PALETTE_PINK.put(COLOR_ON, Color.WHITE);
        PALETTE_PINK.put(COLOR_PATTERN, playColor);
        PALETTE_PINK.put(COLOR_PATTERN_PLAYING, Color.WHITE);
        PALETTE_PINK.put(COLOR_PATTERN_CHAINED, playColorDim);
        PALETTE_PINK.put(COLOR_PATTERN_SELECTION, selectColor);
        PALETTE_PINK.put(COLOR_PATTERN_SELECTED, highlightColor);
        PALETTE_PINK.put(COLOR_PATTERN_NEXT, Color.DIM_YELLOW);
        PALETTE_PINK.put(COLOR_TRACK, selectColor);
        PALETTE_PINK.put(COLOR_TRACK_SELECTION, playColor);
        PALETTE_PINK.put(COLOR_TRACK_MUTED, Color.OFF);
        PALETTE_PINK.put(COLOR_TRACK_SELECTED, Color.WHITE);
        PALETTE_PINK.put(COLOR_TRACK_PLAYING, highlightColor);
        PALETTE_PINK.put(COLOR_TRACK_PLAYING_MUTED, Color.DIM_YELLOW);
        PALETTE_PINK.put(COLOR_STEP_OFF, Color.OFF);
        PALETTE_PINK.put(COLOR_STEP_ON, selectColor);
        PALETTE_PINK.put(COLOR_VALUE_OFF, Color.OFF);
        PALETTE_PINK.put(COLOR_VALUE_ON, playColor);
    }

    public static Map<Integer, Color> PALETTE_BLUE = Maps.newHashMap();
    static {
        Color playColor = Color.BRIGHT_BLUE;
        Color playColorDim = Color.DARK_GRAY;
        Color selectColor = Color.fromIndex(1);
        Color highlightColor = Color.BRIGHT_YELLOW;
        PALETTE_BLUE.put(COLOR_OFF, Color.DARK_GRAY);
        PALETTE_BLUE.put(COLOR_ON, Color.WHITE);
        PALETTE_BLUE.put(COLOR_PATTERN, playColor);
        PALETTE_BLUE.put(COLOR_PATTERN_PLAYING, Color.WHITE);
        PALETTE_BLUE.put(COLOR_PATTERN_CHAINED, playColorDim);
        PALETTE_BLUE.put(COLOR_PATTERN_SELECTION, selectColor);
        PALETTE_BLUE.put(COLOR_PATTERN_SELECTED, highlightColor);
        PALETTE_BLUE.put(COLOR_PATTERN_NEXT, Color.DIM_YELLOW);
        PALETTE_BLUE.put(COLOR_TRACK, selectColor);
        PALETTE_BLUE.put(COLOR_TRACK_SELECTION, playColor);
        PALETTE_BLUE.put(COLOR_TRACK_MUTED, Color.OFF);
        PALETTE_BLUE.put(COLOR_TRACK_SELECTED, Color.WHITE);
        PALETTE_BLUE.put(COLOR_TRACK_PLAYING, highlightColor);
        PALETTE_BLUE.put(COLOR_TRACK_PLAYING_MUTED, Color.DIM_YELLOW);
        PALETTE_BLUE.put(COLOR_STEP_OFF, Color.OFF);
        PALETTE_BLUE.put(COLOR_STEP_ON, selectColor);
        PALETTE_BLUE.put(COLOR_VALUE_OFF, Color.OFF);
        PALETTE_BLUE.put(COLOR_VALUE_ON, playColor);
    }

    public static Map<Integer, Color> PALETTE_GREEN = Maps.newHashMap();
    static {
        Color playColor = Color.DIM_GREEN;
        Color playColorDim = Color.DARK_GRAY;
        Color selectColor = Color.fromIndex(1);
        Color highlightColor = Color.BRIGHT_YELLOW;
        PALETTE_GREEN.put(COLOR_OFF, Color.DARK_GRAY);
        PALETTE_GREEN.put(COLOR_ON, Color.WHITE);
        PALETTE_GREEN.put(COLOR_PATTERN, playColor);
        PALETTE_GREEN.put(COLOR_PATTERN_PLAYING, Color.WHITE);
        PALETTE_GREEN.put(COLOR_PATTERN_CHAINED, playColorDim);
        PALETTE_GREEN.put(COLOR_PATTERN_SELECTION, selectColor);
        PALETTE_GREEN.put(COLOR_PATTERN_SELECTED, highlightColor);
        PALETTE_GREEN.put(COLOR_PATTERN_NEXT, Color.DIM_YELLOW);
        PALETTE_GREEN.put(COLOR_TRACK, selectColor);
        PALETTE_GREEN.put(COLOR_TRACK_SELECTION, playColor);
        PALETTE_GREEN.put(COLOR_TRACK_MUTED, Color.OFF);
        PALETTE_GREEN.put(COLOR_TRACK_SELECTED, Color.WHITE);
        PALETTE_GREEN.put(COLOR_TRACK_PLAYING, highlightColor);
        PALETTE_GREEN.put(COLOR_TRACK_PLAYING_MUTED, Color.DIM_YELLOW);
        PALETTE_GREEN.put(COLOR_STEP_OFF, Color.OFF);
        PALETTE_GREEN.put(COLOR_STEP_ON, selectColor);
        PALETTE_GREEN.put(COLOR_VALUE_OFF, Color.OFF);
        PALETTE_GREEN.put(COLOR_VALUE_ON, playColor);
    }


}
