package net.perkowitz.issho.devices.launchpadpro;

import com.google.common.collect.Lists;
import com.google.common.collect.Maps;
import net.perkowitz.issho.devices.*;
import net.perkowitz.issho.hachi.modules.rhythm.RhythmInterface;

import java.util.List;
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
    public static int SWITCHES_ROW = 5;
    public static int MIDI_CHANNEL_MIN_ROW = 6;
    public static int MIDI_CHANNEL_MAX_ROW = 7;

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

    public static Map<RhythmInterface.Mode, GridButton> modeButtonMap = Maps.newHashMap();
    public static Map<RhythmInterface.Mode, GridPad> modePadMap = Maps.newHashMap();
    static {
        modeButtonMap.put(RhythmInterface.Mode.PATTERN_EDIT, GridButton.at(GridButton.Side.Left, 2));
//        modeButtonMap.put(RhythmInterface.Mode.TEMPO, GridButton.at(GridButton.Side.Left, 2));
//        modeButtonMap.put(RhythmInterface.Mode.PLAY, GridButton.at(GridButton.Side.Left, 3));
        modeButtonMap.put(RhythmInterface.Mode.SAVE, GridButton.at(GridButton.Side.Left, 4));
        modeButtonMap.put(RhythmInterface.Mode.SEQUENCE, GridButton.at(GridButton.Side.Left, 5));
        modeButtonMap.put(RhythmInterface.Mode.SETTINGS, GridButton.at(GridButton.Side.Left, 6));
        modeButtonMap.put(RhythmInterface.Mode.MUTE, GridButton.at(GridButton.Side.Left, 7));

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

    // display color functions
    public static Integer COLOR_STEP = 0;
    public static Integer COLOR_STEP_ON = 1;
    public static Integer COLOR_STEP_PLAYING = 2;
    public static Integer COLOR_TRACK = 3;
    public static Integer COLOR_TRACK_SELECTED = 4;
    public static Integer COLOR_TRACK_PLAYING = 5;
    public static Integer COLOR_TRACK_MUTED = 6;
    public static Integer COLOR_TRACK_MUTED_PLAYING = 7;
    public static Integer COLOR_TRACK_MUTED_SELECTED = 8;
    public static Integer COLOR_PATTERN = 9;
    public static Integer COLOR_PATTERN_SELECTED = 10;
    public static Integer COLOR_PATTERN_PLAYING = 11;
    public static Integer COLOR_PATTERN_CHAINED = 12;
    public static Integer COLOR_PATTERN_SELECTED_PLAYING = 13;
    public static Integer COLOR_SESSION = 14;
    public static Integer COLOR_SESSION_SELECTED = 15;
    public static Integer COLOR_SESSION_NEXT = 16;
    public static Integer COLOR_FILE = 17;
    public static Integer COLOR_FILE_SELECTED = 18;
    public static Integer COLOR_MODE_ACTIVE = 19;
    public static Integer COLOR_MODE_INACTIVE = 20;
    public static Integer COLOR_VALUE = 21;

    public static List<Color> PALETTE_BLUE = Lists.newArrayList(
             Color.OFF,Color.MED_GRAY, Color.BRIGHT_GREEN,                                                      // step
             Color.DARK_GRAY, Color.WHITE, Color.BRIGHT_GREEN, Color.OFF, Color.DIM_GREEN, Color.LIGHT_GRAY,    // track
             Color.DIM_BLUE, Color.WHITE, Color.DIM_GREEN, Color.DARK_GRAY, Color.BRIGHT_GREEN,                 // pattern
             Color.DARK_BLUE, Color.BRIGHT_GREEN, Color.DARK_GRAY,                                              // session
             Color.DARK_GRAY, Color.BRIGHT_GREEN,                                                               // file
             Color.LIGHT_BLUE, Color.DARK_BLUE,                                                                 // gate
             Color.OFF                                                                                          // value
    );

    public static List<Color> PALETTE_RED = Lists.newArrayList(
             Color.OFF,Color.MED_GRAY, Color.BRIGHT_YELLOW,                                                     // step
             Color.DIM_RED, Color.WHITE, Color.BRIGHT_YELLOW, Color.OFF, Color.DIM_YELLOW, Color.LIGHT_GRAY,  // track
             Color.DIM_RED, Color.WHITE, Color.DIM_YELLOW, Color.DARK_GRAY, Color.BRIGHT_YELLOW,                // pattern
             Color.DIM_RED, Color.BRIGHT_YELLOW, Color.DARK_GRAY,                                               // session
             Color.DARK_GRAY, Color.BRIGHT_YELLOW,                                                              // file
             Color.BRIGHT_RED, Color.DARK_GRAY,                                                                 // gate
             Color.OFF                                                                                          // value
    );


}
