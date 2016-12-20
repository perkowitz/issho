package net.perkowitz.issho.hachi.modules.shihai;

import net.perkowitz.issho.devices.GridButton;
import net.perkowitz.issho.devices.GridColor;
import net.perkowitz.issho.devices.GridControl;
import net.perkowitz.issho.devices.GridControlSet;
import net.perkowitz.issho.devices.launchpadpro.Color;
import net.perkowitz.issho.hachi.HachiUtil;

import static net.perkowitz.issho.devices.GridButton.Side.Left;

/**
 * Created by optic on 12/19/16.
 */
public class ShihaiUtil {

    public static Color COLOR_OFF = Color.DARK_BLUE;
    public static Color COLOR_ON = Color.BRIGHT_PINK;
    public static Color COLOR_LOGO = Color.BRIGHT_ORANGE;
    public static Color COLOR_MUTED = HachiUtil.COLOR_UNSELECTED; //Color.DARK_BLUE;
    public static Color COLOR_UNMUTED = HachiUtil.COLOR_SELECTED; //Color.WHITE;
    public static Color COLOR_SESSION = Color.DIM_GREEN;
    public static Color COLOR_SESSION_HIGHLIGHT = Color.WHITE;
    public static Color COLOR_PATTERN = Color.DARK_BLUE;
    public static Color COLOR_PATTERN_HIGHLIGHT = Color.WHITE;
    public static Color COLOR_MEASURE = Color.OFF; //Color.DIM_RED;
    public static Color COLOR_MEASURE_HIGHLIGHT = Color.BRIGHT_PINK;
    public static Color COLOR_TICK = Color.OFF; //Color.fromIndex(10);
    public static Color COLOR_TICK_HIGHLIGHT = Color.DIM_PINK;
    public static Color COLOR_TEMPO = Color.DARK_BLUE;
    public static Color COLOR_TEMPO_HIGHLIGHT = Color.BRIGHT_PINK;

    public static GridControlSet muteControls = GridControlSet.buttonSide(GridButton.Side.Bottom);
    public static GridControlSet patternControls = GridControlSet.padRows(0, 1);
    public static GridControlSet measureControls = GridControlSet.padRow(5);
    public static GridControlSet tickControls = GridControlSet.padRows(6, 7);
    public static GridControlSet tempoControls = GridControlSet.buttonSide(GridButton.Side.Right);

    public static GridControl settingsControl = new GridControl(GridButton.at(Left, 6), null);


}
