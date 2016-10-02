package net.perkowitz.issho.devices;

import java.util.Set;

/**
 * Created by optic on 9/19/16.
 */
public interface GridDisplay {

    public void initialize();
    public void initialize(boolean pads, Set<GridButton.Side> buttonSides);
    public void setPad(GridPad pad, GridColor color);
    public void setButton(GridButton button, GridColor color);

}
