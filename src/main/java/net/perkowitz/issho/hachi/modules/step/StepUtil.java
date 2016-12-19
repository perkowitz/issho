package net.perkowitz.issho.hachi.modules.step;

import com.google.common.collect.Maps;
import net.perkowitz.issho.devices.GridButton;
import net.perkowitz.issho.devices.GridControl;
import net.perkowitz.issho.devices.GridControlSet;
import net.perkowitz.issho.devices.launchpadpro.Color;
import net.perkowitz.issho.hachi.modules.mono.MonoUtil;

import java.util.Map;

import static net.perkowitz.issho.devices.GridButton.Side.Bottom;
import static net.perkowitz.issho.devices.GridButton.Side.Left;
import static net.perkowitz.issho.devices.GridButton.Side.Right;

/**
 * Created by optic on 10/24/16.
 */
public class StepUtil {

    /***** controls *****************/

    public static GridControlSet[] stageColumns = {
            GridControlSet.padColumn(0),
            GridControlSet.padColumn(1),
            GridControlSet.padColumn(2),
            GridControlSet.padColumn(3),
            GridControlSet.padColumn(4),
            GridControlSet.padColumn(5),
            GridControlSet.padColumn(6),
            GridControlSet.padColumn(7)
    };

    public static GridControlSet markerControls = null;
    public static Map<GridControl, Stage.Marker> markerPaletteMap = Maps.newHashMap();
    static {
        markerPaletteMap.put(new GridControl(GridButton.at(Bottom, 0), 0), Stage.Marker.None);
        markerPaletteMap.put(new GridControl(GridButton.at(Bottom, 1), 1), Stage.Marker.Note);
        markerPaletteMap.put(new GridControl(GridButton.at(Bottom, 2), 2), Stage.Marker.Sharp);
        markerPaletteMap.put(new GridControl(GridButton.at(Bottom, 3), 3), Stage.Marker.OctaveUp);
        markerPaletteMap.put(new GridControl(GridButton.at(Bottom, 4), 4), Stage.Marker.VolumeUp);
        markerPaletteMap.put(new GridControl(GridButton.at(Bottom, 5), 5), Stage.Marker.Longer);
        markerPaletteMap.put(new GridControl(GridButton.at(Bottom, 6), 6), Stage.Marker.Repeat);
        markerPaletteMap.put(new GridControl(GridButton.at(Bottom, 7), 7), Stage.Marker.Slide);
        markerControls = new GridControlSet(markerPaletteMap.keySet());
    }

    public static GridControlSet patternControls = GridControlSet.buttonSide(Right);

    // left controls
    public static GridControl muteControl = new GridControl(GridButton.at(Left, 7), null);
    public static GridControl settingsControl = new GridControl(GridButton.at(Left, 6), null);
    public static GridControl saveControl = new GridControl(GridButton.at(Left, 5), null);
//    public static GridControl panicControl = new GridControl(GridButton.at(Left, 4), null);
    public static GridControl currentMarkerDisplayControl = new GridControl(GridButton.at(Left, 4), null);
    public static GridControl altControlsControl = new GridControl(GridButton.at(Left, 3), null);
    public static GridControl savePatternControl = new GridControl(GridButton.at(Left, 2), null);

    // alt controls (used in place of marker controls when alt controls is enabled)
    public static GridControl shiftLeftControl = new GridControl(GridButton.at(Bottom, 0), null);
    public static GridControl shiftRightControl = new GridControl(GridButton.at(Bottom, 1), null);
    public static GridControl randomOrderControl = new GridControl(GridButton.at(Bottom, 2), null);

    // settings
    public static GridControlSet sessionControls = GridControlSet.padRows(MonoUtil.SESSION_MIN_ROW, MonoUtil.SESSION_MAX_ROW);
    public static GridControlSet loadControls = GridControlSet.padRows(MonoUtil.FILE_LOAD_ROW, MonoUtil.FILE_LOAD_ROW);
    public static GridControlSet saveControls = GridControlSet.padRows(MonoUtil.FILE_SAVE_ROW, MonoUtil.FILE_SAVE_ROW);
    public static GridControlSet midiChannelControls = GridControlSet.padRows(MonoUtil.MIDI_CHANNEL_MIN_ROW, MonoUtil.MIDI_CHANNEL_MAX_ROW);




    /***** color palettes **********************************************************/

    public static Integer COLOR_OFF = 15;
    public static Integer COLOR_ON = 16;
    public static Integer COLOR_SESSION = 17;
    public static Integer COLOR_SESSION_ACTIVE = 18;
    public static Integer COLOR_SESSION_NEXT = 19;
    public static Integer COLOR_FILE_LOAD = 20;
    public static Integer COLOR_FILE_SAVE = 21;
    public static Integer COLOR_FILE_ACTIVE = 22;
    public static Integer COLOR_MIDI_CHANNEL = 23;
    public static Integer COLOR_MIDI_CHANNEL_ACTIVE = 24;
    
    public static Map<Integer, Color> PALETTE = Maps.newHashMap();
    static {
        PALETTE.put(COLOR_OFF, Color.DARK_BLUE);
        PALETTE.put(COLOR_ON, Color.BRIGHT_ORANGE);
        PALETTE.put(COLOR_SESSION, Color.fromIndex(55));
        PALETTE.put(COLOR_SESSION_ACTIVE, Color.WHITE);
        PALETTE.put(COLOR_SESSION_NEXT, Color.DARK_GRAY);
        PALETTE.put(COLOR_FILE_LOAD, Color.DIM_GREEN);
        PALETTE.put(COLOR_FILE_SAVE, Color.DIM_RED);
        PALETTE.put(COLOR_FILE_ACTIVE, Color.WHITE);
        PALETTE.put(COLOR_MIDI_CHANNEL, Color.fromIndex(55));
        PALETTE.put(COLOR_MIDI_CHANNEL_ACTIVE, Color.WHITE);
    }

    public static Map<Stage.Marker, Color> MARKER_COLORS = Maps.newHashMap();
    static {
        MARKER_COLORS.put(Stage.Marker.None, Color.OFF);
        MARKER_COLORS.put(Stage.Marker.Note, Color.BRIGHT_BLUE);
        MARKER_COLORS.put(Stage.Marker.Sharp, Color.BRIGHT_BLUE_GREEN);
        MARKER_COLORS.put(Stage.Marker.Flat, Color.DIM_BLUE_GREEN);
        MARKER_COLORS.put(Stage.Marker.OctaveUp, Color.BRIGHT_ORANGE);
        MARKER_COLORS.put(Stage.Marker.OctaveDown, Color.DIM_ORANGE);
        MARKER_COLORS.put(Stage.Marker.VolumeUp, Color.BRIGHT_GREEN);
        MARKER_COLORS.put(Stage.Marker.VolumeDown, Color.DIM_GREEN);
        MARKER_COLORS.put(Stage.Marker.Longer, Color.DARK_BLUE);
        MARKER_COLORS.put(Stage.Marker.Repeat, Color.BRIGHT_PINK);
        MARKER_COLORS.put(Stage.Marker.Skip, Color.BRIGHT_RED);
        MARKER_COLORS.put(Stage.Marker.Slide, Color.DARK_GRAY);
        MARKER_COLORS.put(Stage.Marker.Tie, Color.fromIndex(55));
    }

    public static Color ACTIVE_NOTE_COLOR = Color.WHITE;

}
