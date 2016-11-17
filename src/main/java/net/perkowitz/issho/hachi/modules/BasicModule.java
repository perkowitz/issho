package net.perkowitz.issho.hachi.modules;

import lombok.Getter;
import lombok.Setter;
import net.perkowitz.issho.devices.GridButton;
import net.perkowitz.issho.devices.GridDisplay;
import net.perkowitz.issho.devices.GridListener;
import net.perkowitz.issho.devices.GridPad;
import net.perkowitz.issho.devices.launchpadpro.Color;

/**
 * Created by optic on 9/12/16.
 */
public class BasicModule implements Module, GridListener {

    @Getter @Setter protected GridDisplay display;


    /***** Module interface ****************************************/

    public GridListener getGridListener() {
        return this;
    }

    public void redraw() {}

    public void shutdown() {}

    public void mute(boolean muted) {}

    /***** GridListener interface ****************************************/

    public void onPadPressed(GridPad pad, int velocity) {
        display.setPad(pad, Color.fromIndex(0));
    }

    public void onPadReleased(GridPad pad) {

    }

    public void onButtonPressed(GridButton button, int velocity) {

    }

    public void onButtonReleased(GridButton button) {

    }

}
