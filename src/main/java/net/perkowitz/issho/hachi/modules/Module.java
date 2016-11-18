package net.perkowitz.issho.hachi.modules;

import net.perkowitz.issho.devices.GridDisplay;
import net.perkowitz.issho.devices.GridListener;

/**
 * Created by optic on 9/12/16.
 */
public interface Module {

    public void setDisplay(GridDisplay rhythmDisplay);
    public GridListener getGridListener();
    public void redraw();
    public void shutdown();

}
