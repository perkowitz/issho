package net.perkowitz.issho.devices;

/**
 * Created by optic on 9/4/16.
 */
public interface GridListener {

    public void onPadPressed(GridPad pad, int velocity);
    public void onPadReleased(GridPad pad);
    public void onButtonPressed(GridButton button, int velocity);
    public void onButtonReleased(GridButton button);

}
