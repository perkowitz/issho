package net.perkowitz.issho.hachi.modules;

import net.perkowitz.issho.devices.GridPad;
import net.perkowitz.issho.devices.launchpadpro.Color;

/**
 * Created by optic on 9/12/16.
 */
public class PaletteModule extends BasicModule {

    private boolean upper;


    public PaletteModule(boolean upper) {
        this.upper = upper;
    }


    /***** Module interface ****************************************/

    @Override
    public void redraw() {
        palette();
    }

    @Override
    public void onPadPressed(GridPad pad, int velocity) {
        display.setPad(pad, Color.fromIndex(velocity));
    }


    /***** private implementation ****************************************/

    private void palette() {

        int c = 0;
        if (upper) {
            c = 64;
        }
        for (int y = 0; y < 8; y++) {
            for (int x = 0; x < 8; x++) {
                this.display.setPad(GridPad.at(x, 7-y), Color.fromIndex(c));
                c++;
            }
        }

    }


}
