package net.perkowitz.issho.devices.launchpadpro;

import com.google.common.collect.Maps;
import net.perkowitz.issho.devices.*;
import net.perkowitz.issho.hachi.modules.rhythm.RhythmInterface;

import java.util.Map;



/**
 * Created by optic on 7/10/16.
 */
public class LppRhythmUtil {

    public static boolean debugMode = true;

    // settings module
    public static int SESSIONS_MIN_ROW = 0;
    public static int SESSIONS_MAX_ROW = 1;
    public static int LOAD_ROW = 2;
    public static int SAVE_ROW = 3;
    public static int SWITCHES_ROW = 7;

    // sequence module
    public static int PATTERNS_MIN_ROW = 0;
    public static int PATTERNS_MAX_ROW = 1;
    public static int FILLS_MIN_ROW = 2;
    public static int FILLS_MAX_ROW = 2;
    public static int MODE_ROW = 3;
    public static int TRACKS_MIN_ROW = 4;
    public static int TRACKS_MAX_ROW = 5;
    public static int STEPS_MIN_ROW = 6;
    public static int STEPS_MAX_ROW = 7;

    // display colors
    public static Color COLOR_STEP = Color.OFF;
    public static Color COLOR_STEP_ON = Color.MED_GRAY;
    public static Color COLOR_STEP_PLAYING = Color.BRIGHT_GREEN;

    public static Color COLOR_TRACK = Color.DARK_GRAY;
    public static Color COLOR_TRACK_SELECTED = Color.WHITE;
    public static Color COLOR_TRACK_PLAYING = Color.BRIGHT_GREEN;
    public static Color COLOR_TRACK_MUTED = Color.OFF;
    public static Color COLOR_TRACK_MUTED_PLAYING = Color.DIM_GREEN;
    public static Color COLOR_TRACK_MUTED_SELECTED = Color.LIGHT_GRAY;

    public static Color COLOR_PATTERN = Color.DIM_BLUE;
    public static Color COLOR_PATTERN_SELECTED = Color.WHITE;
    public static Color COLOR_PATTERN_PLAYING = Color.DIM_GREEN;
    public static Color COLOR_PATTERN_CHAINED = Color.DARK_GRAY;
    public static Color COLOR_PATTERN_SELECTED_PLAYING = Color.BRIGHT_GREEN;

    public static Color COLOR_MODE_ACTIVE = Color.LIGHT_BLUE;
    public static Color COLOR_MODE_INACTIVE = Color.DARK_BLUE;

    public static Color COLOR_VALUE = Color.OFF;

    public static Map<RhythmInterface.Mode, GridButton> modeButtonMap = Maps.newHashMap();
    public static Map<RhythmInterface.Mode, GridPad> modePadMap = Maps.newHashMap();
    static {
        modeButtonMap.put(RhythmInterface.Mode.PATTERN_EDIT, GridButton.at(GridButton.Side.Left, 0));
        modeButtonMap.put(RhythmInterface.Mode.TEMPO, GridButton.at(GridButton.Side.Left, 2));
        modeButtonMap.put(RhythmInterface.Mode.PLAY, GridButton.at(GridButton.Side.Left, 3));
        modeButtonMap.put(RhythmInterface.Mode.SAVE, GridButton.at(GridButton.Side.Left, 4));
        modeButtonMap.put(RhythmInterface.Mode.SEQUENCE, GridButton.at(GridButton.Side.Left, 5));
        modeButtonMap.put(RhythmInterface.Mode.SETTINGS, GridButton.at(GridButton.Side.Left, 6));
        modeButtonMap.put(RhythmInterface.Mode.EXIT, GridButton.at(GridButton.Side.Left, 7));

        modePadMap.put(RhythmInterface.Mode.TRACK_MUTE, GridPad.at(0, MODE_ROW));
        modePadMap.put(RhythmInterface.Mode.TRACK_EDIT, GridPad.at(1, MODE_ROW));
        modePadMap.put(RhythmInterface.Mode.STEP_MUTE, GridPad.at(4, MODE_ROW));
        modePadMap.put(RhythmInterface.Mode.STEP_VELOCITY, GridPad.at(5, MODE_ROW));
        modePadMap.put(RhythmInterface.Mode.STEP_JUMP, GridPad.at(6, MODE_ROW));
        modePadMap.put(RhythmInterface.Mode.STEP_PLAY, GridPad.at(7, MODE_ROW));
    }

    public static Map<RhythmInterface.Switch, GridPad> switchPadMap = Maps.newHashMap();
    static {
        switchPadMap.put(RhythmInterface.Switch.INTERNAL_CLOCK_ENABLED, GridPad.at(5, SWITCHES_ROW));
        switchPadMap.put(RhythmInterface.Switch.MIDI_CLOCK_ENABLED, GridPad.at(6, SWITCHES_ROW));
        switchPadMap.put(RhythmInterface.Switch.TRIGGER_ENABLED, GridPad.at(7, SWITCHES_ROW));
    }


}
