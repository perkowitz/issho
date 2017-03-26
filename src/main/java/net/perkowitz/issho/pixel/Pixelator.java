package net.perkowitz.issho.pixel;

import net.perkowitz.issho.devices.GridButton;
import net.perkowitz.issho.devices.GridPad;

/**
 * Created by mikep on 11/24/16.
 */
public interface Pixelator {

    public void onPadPressed(GridPad pad);
    public void onPadReleased(GridPad pad);
    public void onButtonPressed(GridButton button);
    public void onButtonReleased(GridButton button);
//    public void clockTick();

}
