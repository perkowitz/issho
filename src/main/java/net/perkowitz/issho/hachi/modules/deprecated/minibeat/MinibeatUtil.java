package net.perkowitz.issho.hachi.modules.deprecated.minibeat;

import com.google.common.collect.Maps;
import net.perkowitz.issho.devices.GridButton;
import net.perkowitz.issho.devices.GridControl;
import net.perkowitz.issho.devices.GridControlSet;
import net.perkowitz.issho.devices.launchpadpro.Color;

import java.util.Map;

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
    public static GridControl copyControl = new GridControl(GridButton.at(Left, 2), null);


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

    public static Map<Integer, Color> PALETTE_GREEN = Maps.newHashMap();
    static {
        Color playColor = Color.DIM_GREEN;
        Color playColorDim = Color.DARK_GRAY;
        Color selectColor = Color.DIM_BLUE_GREEN;
        PALETTE_GREEN.put(COLOR_OFF, Color.DARK_GRAY);
        PALETTE_GREEN.put(COLOR_ON, Color.WHITE);
        PALETTE_GREEN.put(COLOR_PATTERN, playColor);
        PALETTE_GREEN.put(COLOR_PATTERN_PLAYING, Color.WHITE);
        PALETTE_GREEN.put(COLOR_PATTERN_CHAINED, playColorDim);
        PALETTE_GREEN.put(COLOR_PATTERN_SELECTION, selectColor);
        PALETTE_GREEN.put(COLOR_PATTERN_SELECTED, Color.WHITE);
        PALETTE_GREEN.put(COLOR_PATTERN_NEXT, Color.DIM_YELLOW);
        PALETTE_GREEN.put(COLOR_TRACK, playColor);
        PALETTE_GREEN.put(COLOR_TRACK_SELECTION, selectColor);
        PALETTE_GREEN.put(COLOR_TRACK_MUTED, playColorDim);
        PALETTE_GREEN.put(COLOR_TRACK_SELECTED, Color.WHITE);
        PALETTE_GREEN.put(COLOR_TRACK_PLAYING, Color.BRIGHT_YELLOW);
        PALETTE_GREEN.put(COLOR_TRACK_PLAYING_MUTED, Color.DIM_YELLOW);
        PALETTE_GREEN.put(COLOR_STEP_OFF, Color.OFF);
        PALETTE_GREEN.put(COLOR_STEP_ON, playColor);
        PALETTE_GREEN.put(COLOR_VALUE_OFF, Color.OFF);
        PALETTE_GREEN.put(COLOR_VALUE_ON, selectColor);
    }

    public static Map<Integer, Color> PALETTE_BLUE = Maps.newHashMap();
    static {
        Color playColor = Color.LIGHT_BLUE;
        Color playColorDim = Color.DARK_GRAY;
        Color selectColor = Color.BRIGHT_BLUE_GREEN;
        PALETTE_BLUE.put(COLOR_OFF, Color.DARK_GRAY);
        PALETTE_BLUE.put(COLOR_ON, Color.WHITE);
        PALETTE_BLUE.put(COLOR_PATTERN, playColor);
        PALETTE_BLUE.put(COLOR_PATTERN_PLAYING, Color.WHITE);
        PALETTE_BLUE.put(COLOR_PATTERN_CHAINED, playColorDim);
        PALETTE_BLUE.put(COLOR_PATTERN_SELECTION, selectColor);
        PALETTE_BLUE.put(COLOR_PATTERN_SELECTED, Color.WHITE);
        PALETTE_BLUE.put(COLOR_PATTERN_NEXT, Color.DIM_YELLOW);
        PALETTE_BLUE.put(COLOR_TRACK, playColor);
        PALETTE_BLUE.put(COLOR_TRACK_SELECTION, selectColor);
        PALETTE_BLUE.put(COLOR_TRACK_MUTED, playColorDim);
        PALETTE_BLUE.put(COLOR_TRACK_SELECTED, Color.WHITE);
        PALETTE_BLUE.put(COLOR_TRACK_PLAYING, Color.BRIGHT_YELLOW);
        PALETTE_BLUE.put(COLOR_TRACK_PLAYING_MUTED, Color.DIM_YELLOW);
        PALETTE_BLUE.put(COLOR_STEP_OFF, Color.OFF);
        PALETTE_BLUE.put(COLOR_STEP_ON, playColor);
        PALETTE_BLUE.put(COLOR_VALUE_OFF, Color.OFF);
        PALETTE_BLUE.put(COLOR_VALUE_ON, selectColor);
    }


}
