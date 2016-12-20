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

//    public static final GridButton PLAY_BUTTON = GridButton.at(GridButton.Side.Top, 7);
//    public static final GridButton EXIT_BUTTON = GridButton.at(GridButton.Side.Top, 6);
    public static final GridButton PLAY_BUTTON = GridButton.at(GridButton.Side.Left, 0);
    public static final GridButton EXIT_BUTTON = GridButton.at(GridButton.Side.Left, 1);

    public static Color COLOR_SELECTED = Color.WHITE; //Color.WHITE;//Color.BRIGHT_ORANGE;
    public static Color COLOR_UNSELECTED = Color.DIM_RED; //Color.DIM_ORANGE;// Color.DARK_GRAY;

}
