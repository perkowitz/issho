package net.perkowitz.issho.hachi;

import net.perkowitz.issho.devices.GridButton;
import net.perkowitz.issho.devices.GridColor;
import net.perkowitz.issho.devices.launchpadpro.Color;

import static net.perkowitz.issho.devices.GridButton.Side.Top;

/**
 * Created by optic on 9/19/16.
 */
public class HachiUtil {

    public static final GridButton.Side MODULE_BUTTON_SIDE = Top;

    public static final GridButton PLAY_BUTTON = GridButton.at(GridButton.Side.Left, 0);
    public static final GridButton EXIT_BUTTON = GridButton.at(GridButton.Side.Left, 1);

    public static Color COLOR_SET = Color.WHITE;
    public static Color COLOR_UNSET = Color.DARK_GRAY;
    public static Color COLOR_SELECTED = Color.BRIGHT_YELLOW;
    public static Color COLOR_SELECTED_UNSET = Color.DIM_YELLOW;

    public static int EXIT_PRESS_IN_MILLIS = 2000;

}
