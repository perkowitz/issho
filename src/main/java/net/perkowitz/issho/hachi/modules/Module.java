package net.perkowitz.issho.hachi.modules;

import net.perkowitz.issho.devices.GridDisplay;
import net.perkowitz.issho.devices.GridListener;
import net.perkowitz.issho.util.DisplayUtil;

/**
 * Created by optic on 9/12/16.
 */
public interface Module {

    public void setDisplay(GridDisplay rhythmDisplay);
    public GridListener getGridListener();
    public void redraw();
    public void shutdown();
    public String name();
    public String shortName();
    public DisplayUtil.Color color();
    public String[] buttonLabels();
    public String[] rowLabels();

}
