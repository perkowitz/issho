package net.perkowitz.issho.hachi;


import net.perkowitz.issho.devices.GridColor;
import net.perkowitz.issho.hachi.modules.Module;

import java.util.List;

/**
 * Created by optic on 10/24/16.
 */
public interface Multitrack extends Module {

    public int trackCount();
    public boolean getTrackEnabled(int index);
    public void toggleTrackEnabled(int index);
    public void setTrackEnabled(int index, boolean enabled);
    public GridColor getEnabledColor();

}
