package net.perkowitz.issho.hachi.modules.example;

import com.google.common.collect.Maps;
import net.perkowitz.issho.devices.GridButton;
import net.perkowitz.issho.devices.GridControl;
import net.perkowitz.issho.devices.GridControlSet;
import net.perkowitz.issho.devices.GridPad;
import net.perkowitz.issho.devices.launchpadpro.Color;
import net.perkowitz.issho.hachi.modules.mono.MonoUtil;
import net.perkowitz.issho.hachi.modules.step.Stage;

import java.util.Map;

import static net.perkowitz.issho.devices.GridButton.Side.*;

/**
 * Created by optic on 10/24/16.
 */
public class ExampleUtil {

    /***** controls *****************/

    // a GridControlSet containing all the buttons along the bottom
    public static GridControlSet buttonControls = GridControlSet.buttonSide(Bottom);

    // GridControlSets for rows of pads
    public static GridControlSet onePadRowControls = GridControlSet.padRow(0);
    public static GridControlSet twoPadRowControls = GridControlSet.padRows(2, 3);
    public static GridControlSet partialPadRowControls = GridControlSet.pads(6, 7, 2, 5);

    // a single button
    public static GridControl buttonControl = new GridControl(GridButton.at(Left, 7), null);
    public static GridControl settingsControl = new GridControl(GridButton.at(Left, 6), null);

    // a single pad
    public static GridControl padControl = new GridControl(GridPad.at(0, 5), null);

    // standard settings controls
    public static GridControlSet sessionControls = GridControlSet.padRows(MonoUtil.SESSION_MIN_ROW, MonoUtil.SESSION_MAX_ROW);
    public static GridControlSet loadControls = GridControlSet.padRows(MonoUtil.FILE_LOAD_ROW, MonoUtil.FILE_LOAD_ROW);
    public static GridControlSet saveControls = GridControlSet.padRows(MonoUtil.FILE_SAVE_ROW, MonoUtil.FILE_SAVE_ROW);
    public static GridControlSet midiChannelControls = GridControlSet.padRows(MonoUtil.MIDI_CHANNEL_MIN_ROW, MonoUtil.MIDI_CHANNEL_MAX_ROW);


    /***** colors **********************************************************/

    // give color indices some easy names to refer to
    public static Integer COLOR_OFF = 0;
    public static Integer COLOR_ON = 1;
    public static Integer COLOR_PADS = 2;
    public static Integer COLOR_MORE_PADS = 3;

    // make a palette by setting colors for the named indices
    public static Map<Integer, Color> PALETTE = Maps.newHashMap();
    static {
        PALETTE.put(COLOR_OFF, Color.DARK_GRAY);
        PALETTE.put(COLOR_ON, Color.WHITE);
        PALETTE.put(COLOR_PADS, Color.fromIndex(55));
        PALETTE.put(COLOR_MORE_PADS, Color.BRIGHT_ORANGE);
    }

    // make another palette just for fun
    public static Map<Integer, Color> ANOTHER_PALETTE = Maps.newHashMap();
    static {
        ANOTHER_PALETTE.put(COLOR_OFF, Color.DIM_RED);
        ANOTHER_PALETTE.put(COLOR_ON, Color.BRIGHT_GREEN);
        ANOTHER_PALETTE.put(COLOR_PADS, Color.BRIGHT_BLUE);
        ANOTHER_PALETTE.put(COLOR_MORE_PADS, Color.WHITE);
    }

}
