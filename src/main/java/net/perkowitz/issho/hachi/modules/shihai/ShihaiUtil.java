package net.perkowitz.issho.hachi.modules.shihai;

import com.google.common.collect.Lists;
import net.perkowitz.issho.devices.GridButton;
import net.perkowitz.issho.devices.GridControl;
import net.perkowitz.issho.devices.GridControlSet;
import net.perkowitz.issho.devices.launchpadpro.Color;
import net.perkowitz.issho.hachi.HachiUtil;

import java.util.List;

import static net.perkowitz.issho.devices.GridButton.Side.Left;

/**
 * Created by optic on 12/19/16.
 */
public class ShihaiUtil {

    // these should be <=4 chars
    public static String[] BUTTON_LABELS = new String[]{
            "Play", "Exit", "", "Rset", "Panc", "", "Sett", "Fill",
            "-", "-", "-", "Temp", "-", "-", "-", "-",
            "-", "-", "-", "Mute", "-", "-", "-", "-"
    };

    public static Color COLOR_OFF = Color.DARK_GRAY;
    public static Color COLOR_ON = Color.WHITE;
    public static Color COLOR_LOGO = Color.BRIGHT_ORANGE;
    public static Color COLOR_MUTED = HachiUtil.COLOR_UNSET;
    public static Color COLOR_UNMUTED = HachiUtil.COLOR_SET;
    public static Color COLOR_SESSION = Color.DIM_GREEN;
    public static Color COLOR_SESSION_HIGHLIGHT = Color.WHITE;
    public static Color COLOR_PATTERN = Color.DARK_GRAY;
    public static Color COLOR_PATTERN_HIGHLIGHT = Color.WHITE;
    public static Color COLOR_TICK = Color.OFF;
    public static Color COLOR_TICK_HIGHLIGHT = Color.BRIGHT_YELLOW;
    public static Color COLOR_MEASURE_HIGHLIGHT = Color.WHITE;
    public static Color COLOR_TEMPO = Color.DARK_GRAY;
    public static Color COLOR_TEMPO_HIGHLIGHT = Color.WHITE;

    // main controls
    public static GridControlSet muteControls = GridControlSet.buttonSide(GridButton.Side.Bottom);
    public static GridControlSet patternControls = GridControlSet.padRows(0, 1);
    public static GridControlSet clockControls = GridControlSet.padRows(6, 7);
    public static GridControlSet tempoControls = GridControlSet.buttonSide(GridButton.Side.Right);

    // controls for muting/unmuting multitrack modules
    public static GridControlSet multitrack1 = GridControlSet.padRows(2, 3);
    public static GridControlSet multitrack2 = GridControlSet.padRows(4, 5);
    public static GridControlSet multitrack3 = GridControlSet.padRows(6, 7);
    public static GridControlSet allMultitrack = null;
    public static List<GridControlSet> multitrackControls = Lists.newArrayList(multitrack1, multitrack2, multitrack3);
    static {
        List<GridControl> controls = Lists.newArrayList();
        for (int m = 0; m < multitrackControls.size(); m++) {
            controls.addAll(multitrackControls.get(m).getControls());
        }
        allMultitrack = new GridControlSet(controls);
    }

    // jump controls (can be enabled instead of multitrack3)
    public static GridControlSet jumpControls = GridControlSet.padRows(6, 7);

    // left controls
    public static GridControlSet leftControls = GridControlSet.buttonSide(Left, 2, 7);
    public static GridControl settingsControl = new GridControl(GridButton.at(Left, 6), null);
    public static GridControl panicControl = new GridControl(GridButton.at(Left, 4), null);
    public static GridControl fillControl = new GridControl(GridButton.at(Left, 7), null);
    public static GridControl clockResetControl = new GridControl(GridButton.at(Left, 3), null);


}
