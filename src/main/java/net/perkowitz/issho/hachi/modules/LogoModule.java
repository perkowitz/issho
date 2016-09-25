package net.perkowitz.issho.hachi.modules;

import net.perkowitz.issho.devices.GridColor;
import net.perkowitz.issho.devices.GridPad;
import net.perkowitz.issho.hachi.Graphics;

/**
 * Created by optic on 9/12/16.
 */
public class LogoModule extends BasicModule {

    private GridPad[] pads;
    private GridColor color;

    public LogoModule(GridPad[] pads, GridColor color) {
        this.pads = pads;
        this.color = color;
    }


    /***** Module interface ****************************************/

    @Override
    public void redraw() {
        Graphics.setPads(display, pads, color);
    }

}
