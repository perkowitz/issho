package net.perkowitz.issho.hachi.modules;

import net.perkowitz.issho.devices.GridDisplay;
import net.perkowitz.issho.devices.GridListener;
import net.perkowitz.issho.hachi.models.Memory;

import java.util.List;

/**
 * Created by optic on 9/12/16.
 */
public interface Module {

    public void open();
    public void close();

    public void setDisplay(GridDisplay rhythmDisplay);
    public GridListener getGridListener();

    public void redraw();

    // return pads to be used in a multi-module display
//    public List<GridPad> getSpecialPads(int limit);

    public Memory getMemory();
    public void Save();
    public void Load();

    // get/set colors for display in hachi

}
